package com.vulnuris.eventservice.dto;

import lombok.Data;

/**
 * Matches the CesEventDto schema from the correlation service.
 * Used by POST /ingest to accept events in the same format
 * that Kafka would deliver from the log-ingestion pipeline.
 */
@Data
public class CesEventIngestDTO {

    private Double tsUtc;           // epoch seconds (e.g. 1761742485.0)
    private String tsOriginal;
    private String tzOffset;
    private Integer tsUncertaintyMs;

    private String sourceType;
    private String host;
    private String user;            // CES uses "user", our entity uses "userName"
    private String srcIp;
    private Integer srcPort;
    private String dstIp;
    private Integer dstPort;
    private String protocol;

    private String action;
    private String object;          // CES uses "object", our entity uses "objectValue"
    private String result;
    private Integer severity;
    private String message;

    /** JSON-encoded maps — kept as strings for storage */
    private String metadataJson;
    private String iocsJson;
    private String correlationKeysJson;
    private String rawRefJson;
}
