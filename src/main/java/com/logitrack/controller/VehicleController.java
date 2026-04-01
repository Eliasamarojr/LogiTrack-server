package com.logitrack.controller;

import com.logitrack.common.ApiResponse;
import com.logitrack.dto.VehicleRequestDTO;
import com.logitrack.dto.VehicleResponseDTO;
import com.logitrack.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponseDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> create(@Valid @RequestBody VehicleRequestDTO body) {
        VehicleResponseDTO created = vehicleService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Veículo criado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO body) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.update(id, body), "Veículo atualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Veículo removido"));
    }
}
