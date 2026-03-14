package com.devices.devices_service.controller;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.exception.DeviceInUseException;
import com.devices.devices_service.exception.DeviceNotFoundException;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.DeviceUpdateRequest;
import com.devices.devices_service.mapper.DeviceMapper;
import com.devices.devices_service.mapper.PageDeviceMapper;
import com.devices.devices_service.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DeviceController.class)
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private DeviceMapper deviceMapper;

    @MockitoBean
    private PageDeviceMapper pageDeviceMapper;

    @Test
    void shouldCreateDevice() throws Exception{
        DeviceRequest request = new DeviceRequest()
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE.name());

        Device device = new Device();
        device.setId(1L);
        device.setName("Printer");
        device.setBrand("HP");
        device.setState(com.devices.devices_service.domain.DeviceState.AVAILABLE);
        device.setCreatedAt(Instant.now());

        DeviceResponse response = new DeviceResponse()
                .id(1L)
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE.name());

        when(deviceMapper.toEntity(any(DeviceRequest.class))).thenReturn(device);
        when(deviceService.create(any())).thenReturn(device);
        when(deviceMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnDeviceById() throws Exception {

        Device device = new Device();
        device.setId(1L);
        device.setName("Printer");
        device.setBrand("HP");
        device.setState(com.devices.devices_service.domain.DeviceState.AVAILABLE);
        device.setCreatedAt(Instant.now());

        DeviceResponse response = new DeviceResponse()
                .id(1L)
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE.name());

        when(deviceService.findById(1L)).thenReturn(device);
        when(deviceMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Printer"));
    }

    @Test
    void shouldReturn404WhenDeviceNotFound() throws Exception {

        when(deviceService.findById(99L))
                .thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(get("/devices/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteDevice() throws Exception {

        doNothing().when(deviceService).delete(1L);

        mockMvc.perform(delete("/devices/1"))
                .andExpect(status().isNoContent());

        verify(deviceService).delete(1L);
    }

    @Test
    void shouldReturn400WhenDeviceRequestInvalid() throws Exception {

        DeviceRequest request = new DeviceRequest()
                .name("")
                .brand("")
                .state(DeviceState.AVAILABLE.name());

        mockMvc.perform(
                        post("/devices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenDeviceInUse() throws Exception {

        DeviceRequest request = new DeviceRequest()
                .name("Printer")
                .brand("HP")
                .state(DeviceState.IN_USE.name());

        when(deviceMapper.toEntity(any(DeviceRequest.class))).thenReturn(new Device());
        when(deviceService.create(any()))
                .thenThrow(new DeviceInUseException(1L));

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenIllegalArgument() throws Exception {

        mockMvc.perform(get("/devices?state=INVALID_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenOptimisticLockingFails() throws Exception {

        DeviceUpdateRequest request = new DeviceUpdateRequest()
                .name("Printer Updated")
                .brand("HP")
                .state(DeviceState.AVAILABLE.name())
                .version(0L);

        when(deviceMapper.toEntity(any(DeviceUpdateRequest.class))).thenReturn(new Device());
        when(deviceService.update(eq(1L), any()))
                .thenThrow(new org.springframework.orm.ObjectOptimisticLockingFailureException(Device.class, 1L));

        mockMvc.perform(put("/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn500WhenUnexpectedError() throws Exception {

        when(deviceService.findById(1L))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateDevice() throws Exception{
        DeviceUpdateRequest request = new DeviceUpdateRequest()
                .name("Printer Updated")
                .brand("HP")
                .state(DeviceState.AVAILABLE.name())
                .version(0L);

            Device device = new Device();
            device.setId(1L);
            device.setName("Printer Updated");
            device.setBrand("HP");
            device.setState(com.devices.devices_service.domain.DeviceState.AVAILABLE);

            DeviceResponse response = new DeviceResponse()
                    .id(1L)
                    .name("Printer Updated")
                    .brand("HP")
                    .state(DeviceState.AVAILABLE.name());

            when(deviceMapper.toEntity(any(DeviceUpdateRequest.class))).thenReturn(device);
            when(deviceService.update(eq(1L), any())).thenReturn(device);
            when(deviceMapper.toResponse(any())).thenReturn(response);

            mockMvc.perform(put("/devices/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Printer Updated"));
    }

    @Test
    void shouldReturnPagedDevices() throws Exception {

        mockMvc.perform(get("/devices?page=0&size=10"))
                .andExpect(status().isOk());

        verify(deviceService).findDevices(any(), any(), any());
    }

    @Test
    void shouldReturn400WhenStateInvalid() throws Exception {

        mockMvc.perform(get("/devices?state=INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnDevicesWithSorting() throws Exception {

        mockMvc.perform(get("/devices?page=0&size=10&sort=name,desc"))
                .andExpect(status().isOk());

        verify(deviceService).findDevices(any(), any(), any());
    }


}
