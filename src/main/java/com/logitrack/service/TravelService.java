package com.logitrack.service;

import com.logitrack.domain.Travel;
import com.logitrack.domain.Vehicle;
import com.logitrack.dto.TravelRequestDTO;
import com.logitrack.dto.TravelResponseDTO;
import com.logitrack.exception.BusinessException;
import com.logitrack.repository.TravelRepository;
import com.logitrack.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final VehicleRepository vehicleRepository;

    public TravelService(TravelRepository travelRepository, VehicleRepository vehicleRepository) {
        this.travelRepository = travelRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public TravelResponseDTO create(TravelRequestDTO request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado"));
        validateDatesAndDistance(request);
        Travel travel = new Travel();
        apply(travel, vehicle, request);
        return toDto(travelRepository.save(travel));
    }

    @Transactional(readOnly = true)
    public List<TravelResponseDTO> findAll() {
        return travelRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TravelResponseDTO findById(Long id) {
        return travelRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException("Viagem não encontrada"));
    }

    @Transactional
    public TravelResponseDTO update(Long id, TravelRequestDTO request) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Viagem não encontrada"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado"));
        validateDatesAndDistance(request);
        apply(travel, vehicle, request);
        return toDto(travelRepository.save(travel));
    }

    @Transactional
    public void delete(Long id) {
        if (!travelRepository.existsById(id)) {
            throw new BusinessException("Viagem não encontrada");
        }
        travelRepository.deleteById(id);
    }

    private void validateDatesAndDistance(TravelRequestDTO request) {
        if (request.getArrivalDateTime().isBefore(request.getDepartureDateTime())) {
            throw new BusinessException("Chegada não pode ser anterior à partida");
        }
        if (request.getDistanceKm() == null || request.getDistanceKm().signum() <= 0) {
            throw new BusinessException("Distância deve ser maior que zero");
        }
    }

    private void apply(Travel travel, Vehicle vehicle, TravelRequestDTO request) {
        travel.setVehicle(vehicle);
        travel.setDepartureDateTime(request.getDepartureDateTime());
        travel.setArrivalDateTime(request.getArrivalDateTime());
        travel.setOriginCity(request.getOriginCity().trim());
        travel.setDestinationCity(request.getDestinationCity().trim());
        travel.setDistanceKm(request.getDistanceKm());
    }

    private TravelResponseDTO toDto(Travel travel) {
        Vehicle v = travel.getVehicle();
        TravelResponseDTO dto = new TravelResponseDTO();
        dto.setId(travel.getId());
        dto.setVehicleId(v.getId());
        dto.setVehiclePlate(v.getPlate());
        dto.setVehicleType(v.getType());
        dto.setVehicleModel(v.getModel());
        dto.setDepartureDateTime(travel.getDepartureDateTime());
        dto.setArrivalDateTime(travel.getArrivalDateTime());
        dto.setOriginCity(travel.getOriginCity());
        dto.setDestinationCity(travel.getDestinationCity());
        dto.setDistanceKm(travel.getDistanceKm());
        dto.setCreatedAt(travel.getCreatedAt());
        dto.setUpdatedAt(travel.getUpdatedAt());
        return dto;
    }
}
