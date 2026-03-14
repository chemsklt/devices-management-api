package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.PageDeviceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PageDeviceMapperTest {

    private final DeviceMapper deviceMapper = mock(DeviceMapper.class);
    private final PageDeviceMapper pageDeviceMapper = new PageDeviceMapper(deviceMapper);

    @Test
    void shouldMapPageDeviceResponse(){
        Device device = new Device();
        device.setId(1L);
        device.setName("Printer");

        DeviceResponse response = new DeviceResponse()
                .id(1L)
                .name("Printer");

        when(deviceMapper.toResponse(device)).thenReturn(response);

        Page<Device> page = new PageImpl<>(List.of(device));

        PageDeviceResponse result = pageDeviceMapper.toResponse(page);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Printer");

        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotalElement()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(deviceMapper).toResponse(device);
    }
}
