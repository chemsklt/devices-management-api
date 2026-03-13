package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.PageDeviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PageDeviceMapper {

    private final DeviceMapper deviceMapper;

    public PageDeviceResponse toResponse(Page<Device> devicePage) {

        List<DeviceResponse> devices = devicePage.getContent()
                .stream()
                .map(deviceMapper::toResponse)
                .toList();

        return new PageDeviceResponse()
                .content(devices)
                .page(devicePage.getNumber())
                .size(devicePage.getSize())
                .totalElement(devicePage.getTotalElements())
                .totalPages(devicePage.getTotalPages());
    }
}
