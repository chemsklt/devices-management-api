package com.devices.devices_service.service;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.exception.DeviceInUseException;
import com.devices.devices_service.repository.DeviceRepository;
import com.devices.devices_service.util.DeviceTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void shouldCreateDevice(){
        Device device = DeviceTestDataBuilder.device();
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        Device result = deviceService.create(device);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Printer");

        verify(deviceRepository).save(device);
    }

    @Test
    void shouldFindDeviceById() {

        Device device = DeviceTestDataBuilder.device();

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        Device result = deviceService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingInUseDevice() {

        Device device = DeviceTestDataBuilder.device();
        device.setState(DeviceState.IN_USE);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        assertThatThrownBy(() -> deviceService.update(1L, device))
                .isInstanceOf(DeviceInUseException.class);
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.findById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Device not found");
    }

    @Test
    void shouldThrowExceptionWhenDeletingInUseDevice() {

        Device device = DeviceTestDataBuilder.device();
        device.setState(DeviceState.IN_USE);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        assertThatThrownBy(() -> deviceService.delete(1L))
                .isInstanceOf(DeviceInUseException.class);
    }

    @Test
    void shouldUpdateDevice() {

        Device existing = DeviceTestDataBuilder.device();
        Device updated = DeviceTestDataBuilder.device();
        updated.setName("Scanner");

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(deviceRepository.save(any(Device.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Scanner");

        verify(deviceRepository).save(existing);
    }
}
