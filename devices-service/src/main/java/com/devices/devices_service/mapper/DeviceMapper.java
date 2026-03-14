package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    Device toEntity(DeviceRequest request);
    DeviceResponse toResponse(Device device);

    default OffsetDateTime map(Instant value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
