package com.gimbal.logger;

public enum GimbalStatusType {
    
    INFO("INFO"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILUAR");

    private final String message;

    GimbalStatusType(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
