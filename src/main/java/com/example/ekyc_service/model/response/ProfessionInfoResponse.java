package com.example.ekyc_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProfessionInfoResponse {

    private String professionId;
    private String name;
}
