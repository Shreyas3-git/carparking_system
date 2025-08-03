package com.demo.design.carparking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonResponse
{
    private String message;
    private String errorCode;
    private String status;
    private LocalDateTime timestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public static class CommonResponseBuilder {
        public String message;
        private String errorCode;
        private String status;
        private LocalDateTime timestamp;


        public CommonResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        public CommonResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public CommonResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public CommonResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CommonResponse build() {
            CommonResponse response = new CommonResponse();
            response.message = this.message;
            response.status = this.status;
            response.errorCode = this.errorCode;
            response.timestamp = this.timestamp;
            return response;
        }

    }
}
