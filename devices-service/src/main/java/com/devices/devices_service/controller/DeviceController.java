package com.devices.devices_service.controller;

import com.devices.devices_service.generated.api.DevicesApi;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DeviceController implements DevicesApi {

    private DeviceService deviceService;

    @Override
    public ResponseEntity<DeviceResponse> createdDevice(@Valid DeviceRequest deviceRequest) {
        return DevicesApi.super.createdDevice(deviceRequest);
    }

    @Override
    public ResponseEntity<Void> deleteDevice(Long id) {
        return DevicesApi.super.deleteDevice(id);
    }

    @Override
    public ResponseEntity<DeviceResponse> getDeviceById(String id) {
        return DevicesApi.super.getDeviceById(id);
    }

    @Override
    public ResponseEntity<List<DeviceResponse>> getDevices() {
        return DevicesApi.super.getDevices();
    }

    @Override
    public ResponseEntity<DeviceResponse> updateDevice(Long id, DeviceRequest deviceRequest) {
        return DevicesApi.super.updateDevice(id, deviceRequest);
    }
}
