package com.stormguard.stormguard_api.repository;

import com.stormguard.stormguard_api.dto.AlertResumoDTO;
import com.stormguard.stormguard_api.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("""
        SELECT new com.stormguard.stormguard_api.dto.AlertResumoDTO(
            a.id, a.event, a.status, a.areaDesc, a.severity, a.urgency, a.certainty
        )
        FROM Alert a
        WHERE (:event IS NULL OR LOWER(a.event) LIKE LOWER(CONCAT('%', :event, '%')))
          AND (:status IS NULL OR LOWER(a.status) LIKE LOWER(CONCAT('%', :status, '%')))
          AND (:area IS NULL OR LOWER(a.areaDesc) LIKE LOWER(CONCAT('%', :area, '%')))
    """)
    Page<AlertResumoDTO> findResumoByFilters(String event, String status, String area, Pageable pageable);
}