package com.example.ekyc_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;


/**
 * Entity representing User information.
 * Each User entry includes a UUID identifier and the name of the User.
 * This entity extends {@link AuditMetaData} to include audit-related fields.
 *
 * @author Jannath
 */
@Entity
@Getter
@Setter
@Table(
        name = "user_info",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_full_name", columnList = "full_name")
        }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserInfo extends AuditMetaData {

    /**
     * Primary key of the profession_info table.
     * Automatically generated UUID using time-based strategy.
     */
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @Column(name = "full_name", length = 60, nullable = false)
    private String fullName;

    /**
     * Date of birth of the user.
     */
    @Column(name = "dob")
    private LocalDate dateOfBirth;

    /**
     * Profession info (mandatory, 1:1 relationship)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_id", referencedColumnName = "id", nullable = false)
    private ProfessionInfo professionInfo;
}
