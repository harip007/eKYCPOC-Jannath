package com.example.ekyc_service.model.process;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class VerificationDetails {

    private String fullName;
    private String registrationNo;
    private String registrationDate;
    private String picValidity;
}
