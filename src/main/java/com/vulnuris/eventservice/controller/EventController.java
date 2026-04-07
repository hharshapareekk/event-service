package com.vulnuris.eventservice.controller;

import com.vulnuris.eventservice.dto.EventRequestDTO;
import com.vulnuris.eventservice.dto.EventResponseDTO;
import com.vulnuris.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    /**
     * List or search events. Without query params returns all events (time-ordered).
     * Use filters for the "search API" requirement.
     */
    @GetMapping
    public List<EventResponseDTO> listOrSearch(
            @RequestParam(required = false) Long bundleId,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String srcIp,
            @RequestParam(required = false) Integer minSeverity,
            @RequestParam(required = false) Integer maxSeverity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromUtc,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toUtc,
            @RequestParam(required = false) String q
    ) {
        boolean anyFilter = bundleId != null || (sourceType != null && !sourceType.isBlank())
                || (userName != null && !userName.isBlank()) || (host != null && !host.isBlank())
                || (srcIp != null && !srcIp.isBlank()) || minSeverity != null || maxSeverity != null
                || fromUtc != null || toUtc != null || (q != null && !q.isBlank());
        if (anyFilter) {
            return eventService.search(bundleId, sourceType, userName, host, srcIp, minSeverity, maxSeverity,
                    fromUtc, toUtc, q);
        }
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    /** Convenience: events for one bundle (same as GET /events?bundleId=) */
    @GetMapping("/by-bundle/{bundleId}")
    public List<EventResponseDTO> getEventsByBundle(@PathVariable Long bundleId) {
        return eventService.getEventsByBundle(bundleId);
    }

    @GetMapping("/filter/range")
    public List<EventResponseDTO> getEventsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return eventService.getEventsByTimeRange(start, end);
    }

    @PostMapping
    public EventResponseDTO createEvent(@RequestBody EventRequestDTO dto) {
        return eventService.createEvent(dto);
    }
}
