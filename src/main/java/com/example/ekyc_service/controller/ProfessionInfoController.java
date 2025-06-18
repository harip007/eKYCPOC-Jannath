package com.example.ekyc_service.controller;

import com.example.ekyc_service.model.response.APIResponse;
import com.example.ekyc_service.model.request.ProfessionInfoRequest;
import com.example.ekyc_service.model.response.ProfessionInfoResponse;
import com.example.ekyc_service.service.interfaces.IProfessionInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.example.ekyc_service.constants.APIConstants.ADD_PROFESSION;
import static com.example.ekyc_service.constants.APIConstants.GET_PROFESSIONS;

@RestController
@RequestMapping("api/v1/profession-info")
@AllArgsConstructor
public class ProfessionInfoController {

    private final IProfessionInfoService IProfessionInfoService;

    @PostMapping(ADD_PROFESSION)
    public ResponseEntity<APIResponse> createProfession(@RequestBody final List<ProfessionInfoRequest> requests) {
        String response = IProfessionInfoService.addProfessionInfos(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.builder().message(response).data(Collections.emptyList()).build());
    }

    @GetMapping(GET_PROFESSIONS)
    public ResponseEntity<APIResponse> getAllProfessionDropDown() {
        List<ProfessionInfoResponse> responses = IProfessionInfoService.getAllProfessionInfo();
        return ResponseEntity
                .ok(APIResponse.builder().message("Profession Info Fetched SuccessFully").data(responses).build());

    }


}
