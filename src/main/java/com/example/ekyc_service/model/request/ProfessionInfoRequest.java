package com.example.ekyc_service.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessionInfoRequest {

    @NotNull(message = "Prof Id shouldn't be null")
    private String profId;

    @NotNull(message = "Name shouldn't be null")
    private String name;
}
