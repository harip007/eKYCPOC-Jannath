package com.example.ekyc_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing user educational and license details.
 * Each record links to a unique user and profession.
 * <p>
 * Author: Jannath
 */
@Entity
@Table(name = "user_license_verification",
        indexes = {
                @Index(name = "idx_license_no", columnList = "license_no")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserLicenseVerification extends AuditMetaData {


    /**
     * Primary key of the license_verification table.
     * Automatically generated UUID using time-based strategy.
     */
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "license_no", length = 10, nullable = false)
    private String licenseNo;

    @Column(name = "reg_no", length = 10, nullable = false)
    private String registrationNo;

    @Column(name = "reg_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "pic_validity", updatable = false)
    private LocalDate picValidity;

    /**
     * User info (mandatory, 1:1 relationship)
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserInfo userInfo;
}
