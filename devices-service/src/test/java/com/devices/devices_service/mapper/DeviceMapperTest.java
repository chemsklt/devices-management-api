package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.generated.model.DevicePatchRequest;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.DeviceUpdateRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.*;

public class DeviceMapperTest {
    private final DeviceMapper mapper = Mappers.getMapper(DeviceMapper.class);


    @Test
    void shouldMapRequestToEntity() {
        DeviceRequest request = new DeviceRequest()
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE.name());

        Device device = mapper.toEntity(request);

        assertThat(device.getName()).isEqualTo("Printer");
        assertThat(device.getBrand()).isEqualTo("HP");
        assertThat(device.getState()).isEqualTo(DeviceState.AVAILABLE);
        assertThat(device.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMapEntityToResponse() {

        Device device = Device.builder()
                .id(1L)
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE)
                .createdAt(Instant.now())
                .build();

        DeviceResponse response = mapper.toResponse(device);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Printer");
        assertThat(response.getBrand()).isEqualTo("HP");
        assertThat(response.getState()).isEqualTo("AVAILABLE");
    }

    @Test
    void shouldMapInstantToOffsetDateTime() {

        Instant instant = Instant.now();

        OffsetDateTime result = mapper.map(instant);

        assertThat(result).isEqualTo(instant.atOffset(ZoneOffset.UTC));
    }

    @Test
    void shouldReturnNullWhenInstantIsNull() {

        OffsetDateTime result = mapper.map(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapUpdateRequestToEntity() {

        DeviceUpdateRequest request = new DeviceUpdateRequest()
                .name("Printer X")
                .brand("HP")
                .state(DeviceState.IN_USE.name())
                .version(2L);

        Device device = mapper.toEntity(request);

        assertThat(device.getName()).isEqualTo("Printer X");
        assertThat(device.getBrand()).isEqualTo("HP");
        assertThat(device.getState()).isEqualTo(DeviceState.IN_USE);
        assertThat(device.getVersion()).isEqualTo(2L);
    }

    @Test
    void shouldPatchOnlyProvidedFields() {

        Device existing = Device.builder()
                .id(1L)
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE)
                .version(1L)
                .createdAt(Instant.now())
                .build();

        DevicePatchRequest patch = new DevicePatchRequest()
                .state(DeviceState.IN_USE.name())
                .version(1L);

        mapper.patchDevice(patch, existing);

        assertThat(existing.getState()).isEqualTo(DeviceState.IN_USE);
        assertThat(existing.getName()).isEqualTo("Printer");
        assertThat(existing.getBrand()).isEqualTo("HP");
    }
}
