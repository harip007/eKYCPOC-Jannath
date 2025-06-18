package com.example.ekyc_service.service.interfaces;

import com.example.ekyc_service.model.request.ProfessionInfoRequest;
import com.example.ekyc_service.model.response.ProfessionInfoResponse;

import java.util.List;

public interface IProfessionInfoService {

    String addProfessionInfos(final List<ProfessionInfoRequest> requests);

    List<ProfessionInfoResponse> getAllProfessionInfo();
}

