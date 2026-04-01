package com.logitrack.controller;

import com.logitrack.common.ApiResponse;
import com.logitrack.dto.TravelRequestDTO;
import com.logitrack.dto.TravelResponseDTO;
import com.logitrack.service.TravelService;
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
@RequestMapping("/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TravelResponseDTO>> create(@Valid @RequestBody TravelRequestDTO body) {
        TravelResponseDTO created = travelService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Viagem criada"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TravelResponseDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(travelService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(travelService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody TravelRequestDTO body) {
        return ResponseEntity.ok(ApiResponse.ok(travelService.update(id, body), "Viagem atualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        travelService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Viagem removida"));
    }
}
