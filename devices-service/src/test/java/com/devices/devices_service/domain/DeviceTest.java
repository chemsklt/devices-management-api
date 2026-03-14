package com.devices.devices_service.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DeviceTest {
    @Test
    void shouldBuildDevice() {

        Device device = Device.builder()
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE)
                .build();

        assertThat(device.getName()).isEqualTo("Printer");
        assertThat(device.getBrand()).isEqualTo("HP");
        assertThat(device.getState()).isEqualTo(DeviceState.AVAILABLE);
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        Device device = new Device();
        device.setName("Printer");
        device.setBrand("HP");
        device.setState(DeviceState.AVAILABLE);

        device.prePersist();

        assertThat(device.getCreatedAt()).isNotNull();
        assertThat(device.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }
}
