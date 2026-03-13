package com.devices.devices_service.mapper;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.generated.model.DeviceRequest;
import com.devices.devices_service.generated.model.DeviceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    Device toEntity(DeviceRequest request);
    DeviceResponse toResponse(Device device);
//    default com.devices.devices_service.domain.DeviceState toDomainState(
//            com.devices.devices_service.generated.model.DeviceState state) {
//
//        if (state == null) {
//            return null;
//        }
//
//        return com.devices.devices_service.domain.DeviceState.valueOf(state.name());
//    }
}
