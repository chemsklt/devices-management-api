package com.devices.devices_service.util;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;

import java.time.Instant;

public class DeviceTestDataBuilder {

    public static Device device(){
        Device device = new Device();
        device.setId(1L);
        device.setName("Printer");
        device.setBrand("HP");
        device.setState(DeviceState.AVAILABLE);
        device.setCreatedAt(Instant.now());
        device.setVersion(0L);
        return device;
    }
}
