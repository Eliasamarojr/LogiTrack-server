package com.logitrack.service;

import com.logitrack.domain.Vehicle;
import com.logitrack.dto.VehicleRequestDTO;
import com.logitrack.dto.VehicleResponseDTO;
import com.logitrack.exception.BusinessException;
import com.logitrack.repository.TravelRepository;
import com.logitrack.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final TravelRepository travelRepository;

    public VehicleService(VehicleRepository vehicleRepository, TravelRepository travelRepository) {
        this.vehicleRepository = vehicleRepository;
        this.travelRepository = travelRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> findAll() {
        return vehicleRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponseDTO findById(Long id) {
        return vehicleRepository.findById(id).map(this::toDto).orElseThrow(() -> new BusinessException("Veículo não encontrado"));
    }

    @Transactional
    public VehicleResponseDTO create(VehicleRequestDTO request) {
        String plate = normalizePlate(request.getPlate());
        vehicleRepository.findByPlateIgnoreCase(plate).ifPresent(v -> {
            throw new BusinessException("Já existe veículo com esta placa");
        });
        Vehicle v = new Vehicle();
        v.setPlate(plate);
        v.setType(request.getType());
        v.setModel(request.getModel().trim());
        return toDto(vehicleRepository.save(v));
    }

    @Transactional
    public VehicleResponseDTO update(Long id, VehicleRequestDTO request) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new BusinessException("Veículo não encontrado"));
        String plate = normalizePlate(request.getPlate());
        vehicleRepository.findByPlateIgnoreCase(plate).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new BusinessException("Já existe outro veículo com esta placa");
            }
        });
        v.setPlate(plate);
        v.setType(request.getType());
        v.setModel(request.getModel().trim());
        return toDto(vehicleRepository.save(v));
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new BusinessException("Veículo não encontrado");
        }
        if (travelRepository.existsByVehicle_Id(id)) {
            throw new BusinessException("Não é possível excluir: existem viagens vinculadas a este veículo");
        }
        vehicleRepository.deleteById(id);
    }

    private static String normalizePlate(String plate) {
        if (plate == null) {
            return "";
        }
        return plate.trim().toUpperCase();
    }

    private VehicleResponseDTO toDto(Vehicle v) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(v.getId());
        dto.setPlate(v.getPlate());
        dto.setType(v.getType());
        dto.setModel(v.getModel());
        return dto;
    }
}
