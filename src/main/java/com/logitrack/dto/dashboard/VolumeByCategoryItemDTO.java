package com.logitrack.dto.dashboard;

import java.math.BigDecimal;

public class VolumeByCategoryItemDTO {

    private String category;
    private BigDecimal totalKm;
    private Long travelCount;

    public VolumeByCategoryItemDTO() {
    }

    public VolumeByCategoryItemDTO(String category, BigDecimal totalKm, Long travelCount) {
        this.category = category;
        this.totalKm = totalKm;
        this.travelCount = travelCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(BigDecimal totalKm) {
        this.totalKm = totalKm;
    }

    public Long getTravelCount() {
        return travelCount;
    }

    public void setTravelCount(Long travelCount) {
        this.travelCount = travelCount;
    }
}
