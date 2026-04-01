package com.logitrack.repository;

import com.logitrack.domain.Travel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, Long> {

    boolean existsByVehicle_Id(Long vehicleId);

    @Query("SELECT COALESCE(SUM(t.distanceKm), 0) FROM Travel t")
    BigDecimal sumTotalDistanceKm();

    @Query("SELECT COALESCE(SUM(t.distanceKm), 0) FROM Travel t WHERE t.vehicle.id = :vehicleId")
    BigDecimal sumTotalDistanceKmByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query(value = """
            SELECT v.vehicle_type,
                   COALESCE(SUM(t.distance_km), 0),
                   COUNT(t.id)
            FROM vehicle v
            LEFT JOIN travel t ON t.vehicle_id = v.id
            GROUP BY v.vehicle_type
            ORDER BY v.vehicle_type
            """, nativeQuery = true)
    List<Object[]> sumVolumeGroupedByVehicleTypeRows();

    @Query(value = """
            SELECT v.id,
                   v.plate,
                   v.vehicle_type,
                   v.model,
                   COALESCE(SUM(t.distance_km), 0)
            FROM vehicle v
            LEFT JOIN travel t ON t.vehicle_id = v.id
            GROUP BY v.id, v.plate, v.vehicle_type, v.model
            ORDER BY COALESCE(SUM(t.distance_km), 0) DESC
            """, nativeQuery = true)
    List<Object[]> findTopVehicleRows(Pageable pageable);
}
