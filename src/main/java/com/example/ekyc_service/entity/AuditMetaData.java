package com.example.ekyc_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public abstract class AuditMetaData {

    /**
     * The variable holds the userName.
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * The variable holds the userName.
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * The variable holds the created time.
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * The variable holds the unique identifier of the user at time of update.
     */
    @LastModifiedBy
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", length = 50)
    private Boolean isDeleted = false;

    @Column(name = "is_active", length = 50)
    private Boolean isActive = true;

    @PrePersist
    public void before() {
        if (createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

}
