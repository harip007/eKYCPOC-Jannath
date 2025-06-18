package com.example.ekyc_service.mapper;

import com.example.ekyc_service.entity.UserInfo;
import com.example.ekyc_service.model.request.AddUserInfoRequest;
import org.mapstruct.Mapper;

@Mapper
public interface UserInfoMapper {

    UserInfo convertAddUserReqToEntity(AddUserInfoRequest addUserInfoRequest);


}
