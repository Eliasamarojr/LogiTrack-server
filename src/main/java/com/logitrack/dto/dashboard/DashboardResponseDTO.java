package com.logitrack.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponseDTO {

    private BigDecimal totalKm;
    private List<VolumeByCategoryItemDTO> volumeByCategory;
    private List<NextMaintenanceItemDTO> nextMaintenances;
    private TopVehicleDTO topVehicle;
    private BigDecimal monthlyMaintenanceCost;

    public BigDecimal getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(BigDecimal totalKm) {
        this.totalKm = totalKm;
    }

    public List<VolumeByCategoryItemDTO> getVolumeByCategory() {
        return volumeByCategory;
    }

    public void setVolumeByCategory(List<VolumeByCategoryItemDTO> volumeByCategory) {
        this.volumeByCategory = volumeByCategory;
    }

    public List<NextMaintenanceItemDTO> getNextMaintenances() {
        return nextMaintenances;
    }

    public void setNextMaintenances(List<NextMaintenanceItemDTO> nextMaintenances) {
        this.nextMaintenances = nextMaintenances;
    }

    public TopVehicleDTO getTopVehicle() {
        return topVehicle;
    }

    public void setTopVehicle(TopVehicleDTO topVehicle) {
        this.topVehicle = topVehicle;
    }

    public BigDecimal getMonthlyMaintenanceCost() {
        return monthlyMaintenanceCost;
    }

    public void setMonthlyMaintenanceCost(BigDecimal monthlyMaintenanceCost) {
        this.monthlyMaintenanceCost = monthlyMaintenanceCost;
    }
}
