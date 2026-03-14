package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class DeviceMapperTest {
    @Autowired
    private DeviceMapper mapper;

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
}
