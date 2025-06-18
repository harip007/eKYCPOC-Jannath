package com.example.ekyc_service.controller;

import com.example.ekyc_service.annotations.NotNullOrEmpty;
import com.example.ekyc_service.model.request.AddUserInfoRequest;
import com.example.ekyc_service.model.response.APIResponse;
import com.example.ekyc_service.model.response.AddUserResponse;
import com.example.ekyc_service.model.response.ScrapingProgressResponse;
import com.example.ekyc_service.service.interfaces.IUserInfoService;
import com.example.ekyc_service.utility.ScrapingProgressTracker;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.ekyc_service.constants.APIConstants.*;
import static com.example.ekyc_service.constants.ErrorConstants.CATEGORY_SHOULD_NOT_NULL;
import static com.example.ekyc_service.constants.MessageConstants.USER_FETCHED_SUCCESSFULLY;
import static com.example.ekyc_service.constants.ParamConstants.CATEGORY;

/**
 * REST controller for user information related endpoints.
 * <p>
 * Handles user creation, fetching users by category, and tracking scraping progress asynchronously.
 * </p>
 * <p>
 * All endpoints produce JSON responses wrapped in {@link APIResponse} or specialized response DTOs.
 * </p>
 *
 * Author: Jannath
 */
@RestController
@RequestMapping(ROOT_USER)
@AllArgsConstructor
public class UserInfoController {

    private final IUserInfoService userService;
    private final ScrapingProgressTracker scrapingProgressTracker;

    @PostMapping(ADD_USER)
    public ResponseEntity<APIResponse> addUserInfo(@Valid @RequestBody final AddUserInfoRequest addUserInfoRequest) {

        AddUserResponse response = userService.addUser(addUserInfoRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED).body(APIResponse.builder().message("User Info Process").data(response).build());
    }

    @GetMapping(GET_USER)
    public ResponseEntity<APIResponse> getUserInfo(@Valid @NotNullOrEmpty(message = CATEGORY_SHOULD_NOT_NULL)
                                                       @RequestParam(value = CATEGORY) final String category) {
        List<?> userInfos = userService.getAllUserInfo(category);
        return ResponseEntity
                .ok(APIResponse.builder().message(USER_FETCHED_SUCCESSFULLY).data(userInfos).build());
    }

    /**
     * Endpoint to get scraping progress for a given userKey.
     * <p>
     * Queries the {@link ScrapingProgressTracker} for current progress percentage.
     * Maps the progress integer to a status string among COMPLETED, FAILED, NOT_STARTED, or IN_PROGRESS.
     * </p>
     *
     * @param userKey unique key identifying the user scraping operation
     * @return ResponseEntity containing {@link ScrapingProgressResponse} with status and progress
     */
    @GetMapping(SCRAPING_PROGRESS)
    public ResponseEntity<ScrapingProgressResponse> getScrapingProgress(@RequestParam String userKey) {
        int progress = scrapingProgressTracker.getProgress(userKey);

        String status = switch (progress) {
            case 100 -> "RECORD FOUND";
            case -1 -> "NO RECORD FOUND";
            case 0 -> "NOT_STARTED";
            default -> "IN_PROGRESS";
        };

        return ResponseEntity.ok(new ScrapingProgressResponse(status, progress));
    }

}
