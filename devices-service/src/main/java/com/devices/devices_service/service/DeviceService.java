package com.devices.devices_service.service;

import com.devices.devices_service.domain.Device;
import com.devices.devices_service.domain.DeviceState;
import com.devices.devices_service.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Device create(Device device){
        log.info("Creating device with name={} and brand={}", device.getName(), device.getBrand());
        Device savedDevice = deviceRepository.save(device);
        log.info("Device creating successfully with id={}",device.getId());
        return savedDevice;
    }

    public List<Device> findAll(){
        log.info("Fetch all devices");
        return deviceRepository.findAll();
    }

    public Device findById(Long id){
        log.info("Fetching device with id={}", id);
        return deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id={}", id);
                    return new RuntimeException("Device not found");
                });
    }

    public Device update(Long id, Device updatedDevice){
        log.info("Updating device id={}", id);
        Device existing =findById(id);

        if (existing.getState() == DeviceState.IN_USE){
            log.warn("Attempt to update IN_USE device id={}", id);
            throw new RuntimeException("Cannot modify device that is IN_USE");
        }

        existing.setName(updatedDevice.getName());
        existing.setBrand(updatedDevice.getBrand());
        existing.setState(updatedDevice.getState());

        Device savedDevice = deviceRepository.save(existing);
        log.info("Device updated successfully id={}", id);
        return savedDevice;

    }

    public void delete(Long id){
        log.info("Deleting device id={}", id);
        Device device = findById(id);

        if (device.getState() == DeviceState.IN_USE) {
            log.warn("Attempt to delete IN_USE device id={}", id);
            throw new RuntimeException("Cannot delete device that is IN_USE");
        }
        deviceRepository.delete(device);
        log.info("Device deleted successfully id={}", id);
    }
}
