package com.devices.devices_service.controller;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.exception.DeviceNotFoundException;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.DeviceState;
import com.devices.devices_service.mapper.DeviceMapper;
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

@WebMvcTest(DeviceController.class)
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private DeviceMapper deviceMapper;

    @Test
    void shouldCreateDevice() throws Exception{
        DeviceRequest request = new DeviceRequest()
                .name("Printer")
                .brand("HP")
                .state(DeviceState.AVAILABLE);

        Device device = new Device();
        device.setId(1L);
        device.setName("Printer");
        device.setBrand("HP");
        device.setState(com.devices.devices_service.domain.DeviceState.AVAILABLE);
        device.setCreated_at(Instant.now());

        DeviceResponse response = new DeviceResponse()
                .id(1L)
                .name("Printer")
                .brand("HP")
                .state(com.devices.devices_service.generated.model.DeviceState.AVAILABLE);

        when(deviceMapper.toEntity(any())).thenReturn(device);
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
        device.setCreated_at(Instant.now());

        DeviceResponse response = new DeviceResponse()
                .id(1L)
                .name("Printer")
                .brand("HP")
                .state(com.devices.devices_service.generated.model.DeviceState.AVAILABLE);

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
                .state(DeviceState.AVAILABLE);

        mockMvc.perform(
                        post("/devices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


}
