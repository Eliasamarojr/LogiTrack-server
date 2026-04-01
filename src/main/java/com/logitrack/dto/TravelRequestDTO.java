package com.logitrack.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TravelRequestDTO {

    @NotNull(message = "Veículo é obrigatório")
    private Long vehicleId;

    @NotNull(message = "Data/hora de partida é obrigatória")
    private LocalDateTime departureDateTime;

    @NotNull(message = "Data/hora de chegada é obrigatória")
    private LocalDateTime arrivalDateTime;

    @NotBlank(message = "Cidade de origem é obrigatória")
    private String originCity;

    @NotBlank(message = "Cidade de destino é obrigatória")
    private String destinationCity;

    @NotNull(message = "Distância é obrigatória")
    @DecimalMin(value = "0.01", message = "Distância deve ser maior que zero")
    private BigDecimal distanceKm;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }
}
