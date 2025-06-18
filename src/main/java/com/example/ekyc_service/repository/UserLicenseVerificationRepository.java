package com.example.ekyc_service.repository;

import com.example.ekyc_service.entity.UserLicenseVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserLicenseVerificationRepository extends JpaRepository<UserLicenseVerification, UUID> {

    boolean existsByLicenseNo(final String licenseNo);
}
