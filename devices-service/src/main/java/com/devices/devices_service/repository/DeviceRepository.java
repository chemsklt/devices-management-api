package com.devices.devices_service.repository;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Page<Device> findByState(DeviceState state, Pageable pageable);
}
