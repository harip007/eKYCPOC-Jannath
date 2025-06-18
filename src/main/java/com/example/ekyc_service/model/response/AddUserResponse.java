package com.example.ekyc_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddUserResponse {

    private String message;
    private String userKey;
    private String status;
}
