package com.devices.devices_service.controller;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.generated.api.DevicesApi;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.PageDeviceResponse;
import com.devices.devices_service.mapper.DeviceMapper;
import com.devices.devices_service.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DeviceController implements DevicesApi {

    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    @Override
    public ResponseEntity<DeviceResponse> createdDevice(@Valid DeviceRequest deviceRequest) {
        log.info("API request to create device");
        Device device = deviceMapper.toEntity(deviceRequest);
        Device saved = deviceService.create(device);

        DeviceResponse response = deviceMapper.toResponse(saved);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteDevice(Long id) {
        log.info("API request to delete device id={}", id);
        deviceService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<DeviceResponse> getDeviceById(Long id) {
        log.info("API request to fetch device id={}", id);

        Device device = deviceService.findById(id);

        return ResponseEntity.ok(deviceMapper.toResponse(device));
    }

    @Override
    public ResponseEntity<PageDeviceResponse> getDevices(Integer page, Integer size, String sort, DeviceState state) {
        log.info("API request to fetch all devices page={} size={} sort={}", page, size, sort);

        Sort sortObj = Sort.by("createdAt").descending();

        if (sort != null) {
            String[] parts = sort.split(",");
            sortObj = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Device> devicePage = deviceService.findDevices(state, pageable);

        List<DeviceResponse> devices = devicePage
                .getContent()
                .stream()
                .map(deviceMapper::toResponse)
                .toList();

        PageDeviceResponse response = new PageDeviceResponse()
                .content(devices)
                .page(devicePage.getNumber())
                .size(devicePage.getSize())
                .totalElement(devicePage.getTotalElements())
                .totalPages(devicePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DeviceResponse> updateDevice(Long id, DeviceRequest deviceRequest) {
        log.info("API request to update device id={}", id);

        Device updated = deviceMapper.toEntity(deviceRequest);
        Device saved = deviceService.update(id, updated);

        return ResponseEntity.ok(deviceMapper.toResponse(saved));
    }
}
