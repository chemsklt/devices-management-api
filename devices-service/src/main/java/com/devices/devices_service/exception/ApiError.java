package com.devices.devices_service.exception;

import java.time.OffsetDateTime;

public record ApiError(int status, String message, String path, OffsetDateTime timestamp) {}
