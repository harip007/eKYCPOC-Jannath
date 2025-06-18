package com.example.ekyc_service.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLicenseVerificationResponse {

    private String licenseNo;
    private String registrationNo;
    private LocalDate registrationDate;
    private LocalDate picValidity;
    private UserInfoResponse userInfoResponse;
}
