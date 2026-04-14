package com.vulnuris.eventservice.dto;

import lombok.Data;

import java.util.List;

/**
 * Wrapper for the POST /ingest endpoint.
 * Accepts a bundle name/description and a list of CES-formatted events.
 */
@Data
public class IngestRequestDTO {

    private String bundleName;
    private String description;

    /** List of events in CES format */
    private List<CesEventIngestDTO> events;
}
