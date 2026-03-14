package com.devices.devices_service.controller;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.generated.api.DevicesApi;
import com.devices.devices_service.generated.model.*;
import com.devices.devices_service.mapper.DeviceMapper;
import com.devices.devices_service.mapper.PageDeviceMapper;
import com.devices.devices_service.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DeviceController implements DevicesApi {

    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;
    private final PageDeviceMapper pageDeviceMapper;

    @Override
    public ResponseEntity<DeviceResponse> createdDevice(@Valid @RequestBody DeviceRequest deviceRequest) {
        log.info("Creating device name={} brand={}", deviceRequest.getName(), deviceRequest.getBrand());
        Device device = deviceMapper.toEntity(deviceRequest);
        Device saved = deviceService.create(device);
        DeviceResponse response = deviceMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteDevice(Long id) {
        log.info("Deleting device id={}", id);
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<DeviceResponse> getDeviceById(Long id) {
        log.info("Fetching device id={}", id);
        Device device = deviceService.findById(id);
        return ResponseEntity.ok(deviceMapper.toResponse(device));
    }

    @Override
    public ResponseEntity<PageDeviceResponse> getDevices(Integer page, Integer size, String sort, String state, String brand) {
        log.info("Fetching all devices page={} size={} sort={}", page, size, sort);
        Pageable pageable = buildPageable(page, size, sort);
        Page<Device> devicePage = deviceService.findDevices(parseState(state), brand, pageable);

        return ResponseEntity.ok(pageDeviceMapper.toResponse(devicePage));
    }

    @Override
    public ResponseEntity<DeviceResponse> updateDevice(Long id, DeviceUpdateRequest deviceRequest) {
        log.info("Updating device id={}", id);
        Device updated = deviceMapper.toEntity(deviceRequest);
        Device saved = deviceService.update(id, updated);
        return ResponseEntity.ok(deviceMapper.toResponse(saved));
    }

    @Override
    public ResponseEntity<DeviceResponse> patchDevice(Long id, DevicePatchRequest devicePatchRequest) {
        log.info("Partially Updating device id={}", id);
        Device saved = deviceService.patch(id, devicePatchRequest);
        return ResponseEntity.ok(deviceMapper.toResponse(saved));
    }


    private Pageable buildPageable(Integer page, Integer size, String sort) {

        Sort sortObj = Sort.by("createdAt").descending();

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0];
            Sort.Direction direction =
                    parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;

            sortObj = Sort.by(direction, field);
        }

        return PageRequest.of(page, size, sortObj);
    }

    private DeviceState parseState(String state) {

        if (state == null || state.isBlank()) {
            return null;
        }
        try {
            return DeviceState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid device state: " + state);
        }
    }
}
