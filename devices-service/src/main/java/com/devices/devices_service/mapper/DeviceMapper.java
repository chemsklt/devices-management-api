package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.generated.model.DevicePatchRequest;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import com.devices.devices_service.generated.model.DeviceUpdateRequest;
import org.mapstruct.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    Device toEntity(DeviceRequest request);

    DeviceResponse toResponse(Device device);

    @Mapping(target = "version", source = "version")
    Device toEntity(DeviceUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchDevice(DevicePatchRequest request, @MappingTarget Device device);

    default OffsetDateTime map(Instant value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
