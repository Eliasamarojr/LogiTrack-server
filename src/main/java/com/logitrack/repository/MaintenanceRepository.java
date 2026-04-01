package com.logitrack.repository;

import com.logitrack.domain.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findTop5ByOrderByStartDateAsc();

    @Query(value = """
            SELECT COALESCE(SUM(m.estimated_cost), 0)
            FROM maintenance m
            WHERE YEAR(m.start_date) = YEAR(CURRENT_DATE)
              AND MONTH(m.start_date) = MONTH(CURRENT_DATE)
            """, nativeQuery = true)
    BigDecimal sumEstimatedCostCurrentMonthByStartDate();
}
