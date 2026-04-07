package com.vulnuris.eventservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequestDTO {

    private Long bundleId;

    private LocalDateTime tsUtc;
    private String tsOriginal;
    private String tzOffset;
    private Integer tsUncertaintyMs;

    private String sourceType;
    private String host;
    private String userName;
    private String srcIp;
    private Integer srcPort;
    private String dstIp;
    private Integer dstPort;
    private String protocol;

    private String action;
    private String objectValue;
    private String result;
    private Integer severity;
    private String message;

    /** JSON strings (CES iocs, correlation_keys, raw_ref, extra metadata) */
    private String metadata;
    private String iocs;
    private String correlationKeys;
    private String rawRef;
}
