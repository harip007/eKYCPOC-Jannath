package com.example.ekyc_service.repository;

import com.example.ekyc_service.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

    boolean existsByFirstName(final String firstName);
}
