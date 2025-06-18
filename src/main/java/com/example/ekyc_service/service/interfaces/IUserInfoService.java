package com.example.ekyc_service.service.interfaces;

import com.example.ekyc_service.model.request.AddUserInfoRequest;
import com.example.ekyc_service.model.response.AddUserResponse;

import java.util.List;

public interface IUserInfoService {

    AddUserResponse addUser(final AddUserInfoRequest addUserInfoRequest);

    List<?> getAllUserInfo(final String category);
}
