package com.logitrack.dto;

import com.logitrack.domain.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VehicleRequestDTO {

    @NotBlank(message = "Placa é obrigatória")
    @Size(max = 16, message = "Placa deve ter no máximo 16 caracteres")
    private String plate;

    @NotNull(message = "Tipo é obrigatório")
    private VehicleType type;

    @NotBlank(message = "Modelo é obrigatório")
    @Size(max = 120, message = "Modelo deve ter no máximo 120 caracteres")
    private String model;

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
