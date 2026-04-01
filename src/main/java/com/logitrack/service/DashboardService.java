package com.logitrack.service;

import com.logitrack.domain.Maintenance;
import com.logitrack.dto.dashboard.DashboardResponseDTO;
import com.logitrack.dto.dashboard.NextMaintenanceItemDTO;
import com.logitrack.dto.dashboard.TopVehicleDTO;
import com.logitrack.dto.dashboard.VolumeByCategoryItemDTO;
import com.logitrack.repository.MaintenanceRepository;
import com.logitrack.repository.TravelRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final TravelRepository travelRepository;
    private final MaintenanceRepository maintenanceRepository;

    public DashboardService(TravelRepository travelRepository, MaintenanceRepository maintenanceRepository) {
        this.travelRepository = travelRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO buildDashboard(Optional<Long> vehicleIdForTotalKm) {
        DashboardResponseDTO dto = new DashboardResponseDTO();
        BigDecimal totalKm = vehicleIdForTotalKm
                .map(travelRepository::sumTotalDistanceKmByVehicleId)
                .orElseGet(travelRepository::sumTotalDistanceKm);
        dto.setTotalKm(totalKm != null ? totalKm : BigDecimal.ZERO);

        List<VolumeByCategoryItemDTO> volume = new ArrayList<>();
        for (Object[] row : travelRepository.sumVolumeGroupedByVehicleTypeRows()) {
            VolumeByCategoryItemDTO item = new VolumeByCategoryItemDTO();
            item.setCategory(String.valueOf(row[0]));
            item.setTotalKm(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO);
            item.setTravelCount(row[2] != null ? ((Number) row[2]).longValue() : 0L);
            volume.add(item);
        }
        dto.setVolumeByCategory(volume);

        List<NextMaintenanceItemDTO> next = new ArrayList<>();
        for (Maintenance m : maintenanceRepository.findTop5ByOrderByStartDateAsc()) {
            NextMaintenanceItemDTO n = new NextMaintenanceItemDTO();
            n.setId(m.getId());
            n.setVehicleId(m.getVehicle().getId());
            n.setVehiclePlate(m.getVehicle().getPlate());
            n.setStartDate(m.getStartDate());
            n.setEndDate(m.getEndDate());
            n.setType(m.getType());
            n.setEstimatedCost(m.getEstimatedCost());
            n.setStatus(m.getStatus());
            next.add(n);
        }
        dto.setNextMaintenances(next);

        List<Object[]> topRows = travelRepository.findTopVehicleRows(PageRequest.of(0, 1));
        if (!topRows.isEmpty()) {
            Object[] r = topRows.get(0);
            TopVehicleDTO t = new TopVehicleDTO();
            t.setVehicleId(((Number) r[0]).longValue());
            t.setPlate(String.valueOf(r[1]));
            t.setType(String.valueOf(r[2]));
            t.setModel(String.valueOf(r[3]));
            t.setTotalKm(r[4] != null ? new BigDecimal(r[4].toString()) : BigDecimal.ZERO);
            dto.setTopVehicle(t);
        } else {
            dto.setTopVehicle(null);
        }

        BigDecimal monthly = maintenanceRepository.sumEstimatedCostCurrentMonthByStartDate();
        dto.setMonthlyMaintenanceCost(monthly != null ? monthly : BigDecimal.ZERO);
        return dto;
    }
}
