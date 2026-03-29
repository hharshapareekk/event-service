package com.vulnuris.eventservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequestDTO {

    private Long bundleId;

    private LocalDateTime tsUtc;
    private String sourceType;
    private String host;
    private String userName;
    private String srcIp;
    private String dstIp;
    private String action;
    private String objectValue;
    private String result;
    private Integer severity;
    private String message;

    private String metadata;
    private String iocs;
    private String correlationKeys;
    private String rawRef;
}