package com.vulnuris.eventservice.controller;

import com.vulnuris.eventservice.dto.EventRequestDTO;
import com.vulnuris.eventservice.dto.EventResponseDTO;
import com.vulnuris.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class EventController {

    private final EventService eventService;

    //  GET /events
    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    // GET /events/{id}
    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    // ✅ GET /events/by-bundle?bundleId=1
    @GetMapping("/by-bundle")
    public List<EventResponseDTO> getEventsByBundle(@RequestParam Long bundleId) {
        return eventService.getEventsByBundle(bundleId);
    }

    // GET /events/timeline?bundleId=1
    @GetMapping("/timeline")
    public List<EventResponseDTO> getTimeline(@RequestParam Long bundleId) {
        return eventService.getTimeline(bundleId);
    }

    //  GET /events/filter?start=...&end=...
    @GetMapping("/filter")
    public List<EventResponseDTO> getEventsByTimeRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return eventService.getEventsByTimeRange(start, end);
    }

    //  POST /events
    @PostMapping
    public EventResponseDTO createEvent(@RequestBody EventRequestDTO dto) {
        return eventService.createEvent(dto);
    }
}