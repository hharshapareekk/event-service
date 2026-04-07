package com.vulnuris.eventservice.controller;

import com.vulnuris.eventservice.dto.TimelineResponseDTO;
import com.vulnuris.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Spec: {@code GET /timeline} — unified ordered chain for a bundle (all sources together).
 * Optional time window matches project brief filters.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimelineController {

    private final EventService eventService;

    @GetMapping("/timeline")
    public TimelineResponseDTO timeline(
            @RequestParam Long bundleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromUtc,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toUtc
    ) {
        return eventService.getTimeline(bundleId, fromUtc, toUtc);
    }
}
