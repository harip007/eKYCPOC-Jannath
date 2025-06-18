package com.example.ekyc_service.service.impl;

import com.example.ekyc_service.configuration.ScrapingClient;
import com.example.ekyc_service.constants.CategoryConstants;
import com.example.ekyc_service.entity.UserEducationalInfo;
import com.example.ekyc_service.entity.UserInfo;
import com.example.ekyc_service.exception.BadRequestException;
import com.example.ekyc_service.exception.ServerException;
import com.example.ekyc_service.model.request.AddUserInfoRequest;
import com.example.ekyc_service.model.response.AddUserResponse;
import com.example.ekyc_service.model.response.UserInfoResponse;
import com.example.ekyc_service.model.response.UserLicenseVerificationResponse;
import com.example.ekyc_service.repository.UserEducationInfoRepository;
import com.example.ekyc_service.repository.UserInfoRepository;
import com.example.ekyc_service.repository.UserLicenseVerificationRepository;
import com.example.ekyc_service.service.interfaces.IUserInfoService;
import com.example.ekyc_service.utility.ScrapingProgressTracker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.ekyc_service.constants.ErrorConstants.*;
import static com.example.ekyc_service.enums.ScrapingStatus.COMPLETED;
import static com.example.ekyc_service.enums.ScrapingStatus.INITIATED;

@Service
@Slf4j
@AllArgsConstructor
public class UserInfoServiceImpl implements IUserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserEducationInfoRepository userEducationInfoRepository;
    private final UserLicenseVerificationRepository userLicenseVerificationRepository;
    private final ScrapingClient scrapingClient;
    private ScrapingProgressTracker progressTracker;


    /**
     * Adds a user by first validating the request and checking if the user already exists in the house DB.
     * <p>
     * If the user exists, it sets the scraping progress to 100% and returns a success response.
     * If the user doesn't exist, it sets an initial progress, triggers asynchronous scraping,
     * and returns a response with the generated user key to track progress.
     * <p>
     * In case of exceptions, the progress is marked as failed (-1), and appropriate logging is done.
     *
     * @param request the incoming user info request payload
     * @return {@link AddUserResponse} containing status message, progress key, and current status
     * @throws BadRequestException   if the input validation fails
     * @throws ServerException       if any unexpected server-side error occurs
     *
     * @author Jannath
     */
    @Override
    @Transactional
    public AddUserResponse addUser(final AddUserInfoRequest request) {
        String userKey = generateProgressKey(request);
        try {
            validateRequest(request);

            if (isUserPresentInHouseDb(request)) {
                progressTracker.setProgress(userKey, 100);
                log.info("User already exists in database for category: {}", request.getCategory());
                return new AddUserResponse("User data found in house database", userKey, String.valueOf(COMPLETED));
            }

            progressTracker.setProgress(userKey, 10);
            log.info("User not found in DB. Triggering scraping in background...");

            // Asynchronous non-blocking scraping call
            processScrapingAsync(request, userKey);

            return new AddUserResponse("No record found in house database", userKey, String.valueOf(INITIATED));

        } catch (BadRequestException be) {
            progressTracker.setProgress(userKey, -1);
            log.warn("Bad request: {}", be.getMessage());
            throw be;
        } catch (Exception e) {
            progressTracker.setProgress(userKey, -1);
            log.error("Async processing failed: {}", userKey, e);
            CompletableFuture.failedFuture(e);
            log.error("Unexpected error occurred while adding user", e);
            throw new ServerException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async("scrapingTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processScrapingAsync(AddUserInfoRequest request, String userKey) {
        try {
            progressTracker.setProgress(userKey, 25);

            scrapingClient.fetchScrapedUserData(request)
                    .doOnSuccess(data -> {
                        progressTracker.setProgress(userKey, 100);
                        log.info("Scraping completed for userKey: {}", userKey);
                    })
                    .doOnError(error -> {
                        progressTracker.setProgress(userKey, -1);
                        log.error("Scraping failed for userKey: {}", userKey, error);
                    })
                    .subscribe();

        } catch (Exception e) {
            progressTracker.setProgress(userKey, -1);
            log.error("Error in async scraping task for userKey: {}", userKey, e);
        }
    }


    private void validateRequest(final AddUserInfoRequest addUserInfoRequest) {
        if (Objects.isNull(addUserInfoRequest)) {
            throw new BadRequestException(ADD_USER_REQUEST_VALIDATION);
        }
    }

    private boolean isUserPresentInHouseDb(AddUserInfoRequest request) {
        String category = request.getCategory();
        return switch (category) {
            case CategoryConstants.BY_NAME ->
                    userInfoRepository.existsByFirstName(request.getFirstName().toLowerCase());

            case CategoryConstants.BY_LICENSE_NO ->
                    userLicenseVerificationRepository.existsByLicenseNo(request.getLicenseNo());

            case CategoryConstants.BY_RATING ->
                    userEducationInfoRepository.existsByApplicationNo(request.getApplicationNo());

            default -> throw new BadRequestException("Invalid category: " + category);
        };
    }


    @Override
    public List<? extends Object> getAllUserInfo(final String category) {
        try {
            return switch (category) {
                case CategoryConstants.BY_NAME -> getUserInfoResponses();
                case CategoryConstants.BY_LICENSE_NO -> getUserLicenseVerificationResponses();
                case CategoryConstants.BY_RATING -> getUserEducationalInfos();
                default -> {
                    log.warn("Invalid category received: {}", category);
                    throw new IllegalArgumentException(INVALID_CATEGORY + category);
                }
            };
        } catch (Exception e) {
            log.error("Exception occurred while retrieving user info for category: {}", category, e);
            throw new ServerException("Failed to retrieve user info", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generates a deterministic and safe progress key for tracking async scraping tasks.
     * If all identifying fields are missing, a UUID is used to ensure uniqueness.
     *
     * @param request the user info request object
     * @return a unique and traceable progress key
     */
    private String generateProgressKey(AddUserInfoRequest request) {
        final String category = Optional.ofNullable(request.getCategory())
                .map(String::toLowerCase)
                .orElse("unknown");

        final String firstName = Optional.ofNullable(request.getFirstName())
                .map(String::trim)
                .map(String::toLowerCase)
                .orElse("");

        final String lastName = Optional.ofNullable(request.getLastName())
                .map(String::trim)
                .map(String::toLowerCase)
                .orElse("");

        final String birthDate = request.getBirthDate() != null
                ? request.getBirthDate().toString()
                : "nodate";

        // Build base identifier string
        final String base = String.join("|", category, firstName, lastName, birthDate);

        // If everything is empty, fallback to UUID to ensure uniqueness
        if (base.replace("|", "").isBlank()) {
            final String uuidKey = "user_" + UUID.randomUUID();
            log.warn("All identifier fields are missing. Generated random user key: {}", uuidKey);
            return uuidKey;
        }

        // Generate a stable hash-based key
        final String hashedKey = "user_" + Integer.toHexString(base.hashCode());
        log.debug("Generated user progress key [{}] from base: [{}]", hashedKey, base);
        return hashedKey;
    }


    private List<UserInfoResponse> getUserInfoResponses() {
        return userInfoRepository.findAll().stream()
                .map(user -> new UserInfoResponse(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getFullName(),
                        user.getDateOfBirth()))
                .collect(Collectors.toList());
    }

    private List<UserLicenseVerificationResponse> getUserLicenseVerificationResponses() {
        return userLicenseVerificationRepository.findAll().stream()
                .map(license -> {
                    UserInfo user = license.getUserInfo();
                    UserInfoResponse userInfo = new UserInfoResponse(
                            user.getFirstName(),
                            user.getLastName(),
                            user.getFullName(),
                            user.getDateOfBirth());

                    return new UserLicenseVerificationResponse(
                            license.getLicenseNo(),
                            license.getRegistrationNo(),
                            license.getRegistrationDate(),
                            license.getPicValidity(),
                            userInfo);
                })
                .collect(Collectors.toList());
    }

    private List<UserEducationalInfo> getUserEducationalInfos() {
        return userEducationInfoRepository.findAll();
    }


}
