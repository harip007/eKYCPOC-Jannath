package com.example.ekyc_service.repository;

import com.example.ekyc_service.entity.ProfessionInfo;
import com.example.ekyc_service.model.response.ProfessionInfoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessionInfoRepository extends JpaRepository<ProfessionInfo, UUID> {

    @Query(
            """
            SELECT new com.example.ekyc_service.model.response.ProfessionInfoResponse(pi.profId,pi.name)
            FROM ProfessionInfo pi
            WHERE pi.isDeleted = :isDeleted AND pi.isActive = :isActive
            """
    )
    List<ProfessionInfoResponse> findAllProfessionInfo(@Param("isDeleted") Boolean isDeleted, @Param("isActive") Boolean isActive);

    Optional<ProfessionInfo> findByProfId(final String professionId);
}
