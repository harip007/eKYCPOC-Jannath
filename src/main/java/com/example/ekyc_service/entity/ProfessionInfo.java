package com.example.ekyc_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


/**
 * Entity representing professional information.
 * Each profession entry includes a UUID identifier and the name of the profession.
 * This entity extends {@link AuditMetaData} to include audit-related fields.
 *
 * Example: Engineer, Doctor, Teacher, etc.
 *
 * @author Jannath
 */
@Entity
@Table(
        name = "profession_info",
        indexes = {
                @Index(name = "idx_profession_name", columnList = "prof_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProfessionInfo extends AuditMetaData {

    /**
     * Primary key of the profession_info table.
     * Automatically generated UUID using time-based strategy.
     */
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "prof_id", nullable = false, unique = true, updatable = false)
    private String profId;

    /**
     * Name of the profession.
     */
    @Column(name = "prof_name", nullable = false, length = 100)
    private String name;
}
