package com.example.ekyc_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_educational_info", indexes = {
        @Index(name = "idx_education_info_id", columnList = "edu_info_id")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserEducationalInfo extends AuditMetaData {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "edu_info_id", nullable = false, updatable = false)
    private UUID id;

    // Maximum Month Length
    @Column(name = "month", length = 9, nullable = false)
    private String month;

    @Column(name = "year", nullable = false)
    private String year;

    @Column(name = "app_no", nullable = false)
    private String applicationNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_id", referencedColumnName = "id", nullable = false)
    private ProfessionInfo professionInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserInfo userInfo;
}
