package com.devices.devices_service.exception;

public class DeviceInUseException extends RuntimeException{

    public DeviceInUseException(Long id){
        super("Device with id={}"+ id + "is currently IN_USE and cannot be modified");
    }
}
