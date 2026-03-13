package com.devices.devices_service.exception;

public class DeviceNotFoundException extends RuntimeException{

    public DeviceNotFoundException(Long id){
        super("Device not found Exception");
    }
}
