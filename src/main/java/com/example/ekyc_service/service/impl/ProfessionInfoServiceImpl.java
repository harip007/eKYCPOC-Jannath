package com.example.ekyc_service.service.impl;

import com.example.ekyc_service.entity.ProfessionInfo;
import com.example.ekyc_service.exception.BadRequestException;
import com.example.ekyc_service.exception.ServerException;
import com.example.ekyc_service.model.request.ProfessionInfoRequest;
import com.example.ekyc_service.model.response.ProfessionInfoResponse;
import com.example.ekyc_service.repository.ProfessionInfoRepository;
import com.example.ekyc_service.service.interfaces.IProfessionInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ProfessionInfoServiceImpl implements IProfessionInfoService {

    private final ProfessionInfoRepository professionInfoRepository;

    private static final String SUCCESS_MESSAGE = "Profession Info Saved Successfully";

    @Override
    @Transactional
    public String addProfessionInfos(final List<ProfessionInfoRequest> requests) {
        validateRequest(requests);

        List<ProfessionInfo> professionInfoList = requests.stream()
                .map(request -> {
                    ProfessionInfo info = new ProfessionInfo();
                    info.setName(request.getName());
                    info.setProfId(request.getProfId());
                    return info;
                })
                .collect(Collectors.toList());

        try {
            professionInfoRepository.saveAll(professionInfoList);
            log.info("Successfully saved {} profession(s).", professionInfoList.size());
        } catch (Exception e) {
            log.error("Error occurred while saving profession info: {}", e.getMessage(), e);
            throw new ServerException("Failed to save profession information. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return SUCCESS_MESSAGE;
    }

    private void validateRequest(final List<ProfessionInfoRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            log.warn("Empty profession list received in request.");
            throw new BadRequestException("Profession list cannot be empty.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionInfoResponse> getAllProfessionInfo() {
        try {
            List<ProfessionInfoResponse> professionInfoResponses = professionInfoRepository.findAllProfessionInfo(false, true);
            log.info("Retrieved {} profession info entries.", professionInfoResponses.size());
            return professionInfoResponses;
        } catch (Exception e) {
            log.error("Error while fetching profession info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve profession information. Please try again later.");
        }
    }
}
