package com.vulnuris.eventservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponseDTO {

    private Long id;
    private Long bundleId;
    private String bundleKey;

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

    private String metadata;
    private String iocs;
    private String correlationKeys;
    private String rawRef;
}
