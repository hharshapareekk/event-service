package com.vulnuris.eventservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Timeline API payload: ordered events plus lightweight stats for filters/UI.
 * Correlation service can later attach graph hints; this stays a pure time-ordered view.
 */
@Data
@Builder
public class TimelineResponseDTO {

    private Long bundleId;
    private String bundleKey;
    private List<EventResponseDTO> events;
    private Map<String, Object> stats;
    private LocalDateTime rangeStartUtc;
    private LocalDateTime rangeEndUtc;
}
