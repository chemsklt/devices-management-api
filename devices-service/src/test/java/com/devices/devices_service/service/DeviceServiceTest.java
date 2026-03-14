package com.devices.devices_service.service;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.exception.DeviceInUseException;
import com.devices.devices_service.exception.DeviceNotFoundException;
import com.devices.devices_service.repository.DeviceRepository;
import com.devices.devices_service.util.DeviceTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
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

    private Device device;

    @BeforeEach
    void setup() {
        device = DeviceTestDataBuilder.device();
    }
    private static final Long DEVICE_ID = 1L;
    private static final String DEVICE_NAME = "Printer";

    @Test
    @DisplayName("Should create a device successfully")
    void shouldCreateDevice(){
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        Device result = deviceService.create(device);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(DEVICE_NAME);

        verify(deviceRepository).save(device);
    }

    @Test
    @DisplayName("Should return all devices")
    void shouldReturnAllDevices() {
        when(deviceRepository.findAll())
                .thenReturn(List.of(device));

        var result = deviceService.findAll();

        assertThat(result).hasSize(1);
        verify(deviceRepository).findAll();
    }

    @Test
    @DisplayName("Should find a device by id")
    void shouldFindDeviceById() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        Device result = deviceService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find device by state")
    void shouldFindDevicesByState() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByState(DeviceState.AVAILABLE, pageable))
                .thenReturn(page);

        Page<Device> result = deviceService.findDevices(DeviceState.AVAILABLE, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(deviceRepository).findByState(DeviceState.AVAILABLE, pageable);
    }

    @Test
    @DisplayName("Should find device without state filter")
    void shouldFindDevicesWithoutStateFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findAll(pageable))
                .thenReturn(page);

        Page<Device> result = deviceService.findDevices(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(deviceRepository).findAll(pageable);
    }

    @Test
    void shouldThrowOptimisticLockExceptionWhenVersionMismatch() {

        Device existing = DeviceTestDataBuilder.device();
        existing.setVersion(1L);

        Device updated = DeviceTestDataBuilder.device();
        updated.setVersion(2L);

        when(deviceRepository.findById(DEVICE_ID))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> deviceService.update(DEVICE_ID, updated))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void shouldDeleteDevice() {
        device.setState(DeviceState.AVAILABLE);

        when(deviceRepository.findById(DEVICE_ID))
                .thenReturn(Optional.of(device));

        deviceService.delete(DEVICE_ID);

        verify(deviceRepository).delete(device);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingInUseDevice() {
        device.setState(DeviceState.IN_USE);

        when(deviceRepository.findById(DEVICE_ID)).thenReturn(Optional.of(device));

        assertThatThrownBy(() -> deviceService.update(DEVICE_ID, device))
                .isInstanceOf(DeviceInUseException.class);
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {

        when(deviceRepository.findById(DEVICE_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.findById(DEVICE_ID))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Device not found with id: 1");
    }

    @Test
    void shouldThrowExceptionWhenDeletingInUseDevice() {

        device.setState(DeviceState.IN_USE);

        when(deviceRepository.findById(DEVICE_ID))
                .thenReturn(Optional.of(device));

        assertThatThrownBy(() -> deviceService.delete(DEVICE_ID))
                .isInstanceOf(DeviceInUseException.class);
    }

    @Test
    void shouldUpdateDevice() {

        Device existing = DeviceTestDataBuilder.device();
        Device updated = DeviceTestDataBuilder.device();
        updated.setName("Scanner");

        when(deviceRepository.findById(DEVICE_ID))
                .thenReturn(Optional.of(existing));

        when(deviceRepository.save(any(Device.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.update(DEVICE_ID, updated);

        assertThat(result.getName()).isEqualTo("Scanner");

        verify(deviceRepository).save(existing);
    }
}
