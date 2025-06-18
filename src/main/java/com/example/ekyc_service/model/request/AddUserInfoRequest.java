package com.example.ekyc_service.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserInfoRequest {

    @NotNull(message = "Profession Id is required")
    private String professionId;

    @NotNull(message = "Category is required")
    private String category;

    private String firstName;

    private String lastName;

    private String licenseNo;

    private String birthDate;

    private String applicationNo;

    private String month;

    private String year;

}
