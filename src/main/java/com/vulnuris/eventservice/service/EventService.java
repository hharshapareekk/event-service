package com.vulnuris.eventservice.service;

import com.vulnuris.eventservice.dto.EventRequestDTO;
import com.vulnuris.eventservice.dto.EventResponseDTO;
import com.vulnuris.eventservice.entity.Event;
import com.vulnuris.eventservice.repository.BundleRepository;
import com.vulnuris.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final BundleRepository bundleRepository;

    // ✅ CREATE EVENT (DTO → Entity → DTO)
    public EventResponseDTO createEvent(EventRequestDTO dto) {

        Event event = new Event();

        event.setTsUtc(dto.getTsUtc());
        event.setSourceType(dto.getSourceType());
        event.setHost(dto.getHost());
        event.setUserName(dto.getUserName());
        event.setSrcIp(dto.getSrcIp());
        event.setDstIp(dto.getDstIp());
        event.setAction(dto.getAction());
        event.setObjectValue(dto.getObjectValue());
        event.setResult(dto.getResult());
        event.setSeverity(dto.getSeverity());
        event.setMessage(dto.getMessage());

        event.setMetadata(dto.getMetadata());
        event.setIocs(dto.getIocs());
        event.setCorrelationKeys(dto.getCorrelationKeys());
        event.setRawRef(dto.getRawRef());

        // 🔗 Attach bundle
        event.setBundle(
                bundleRepository.findById(dto.getBundleId())
                        .orElseThrow(() -> new RuntimeException("Bundle not found"))
        );

        Event saved = eventRepository.save(event);

        return mapToResponse(saved);
    }

    // ✅ GET ALL EVENTS
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ GET EVENT BY ID
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return mapToResponse(event);
    }

    // ✅ GET EVENTS BY BUNDLE
    public List<EventResponseDTO> getEventsByBundle(Long bundleId) {
        return eventRepository.findByBundleId(bundleId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ TIMELINE (sorted)
    public List<EventResponseDTO> getTimeline(Long bundleId) {
        List<Event> events = eventRepository.findByBundleId(bundleId);

        events.sort(Comparator.comparing(Event::getTsUtc));

        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ FILTER BY TIME RANGE
    public List<EventResponseDTO> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByTsUtcBetween(start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔁 ENTITY → DTO
    private EventResponseDTO mapToResponse(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
               .bundleId(event.getBundle() != null ? event.getBundle().getId() : null)
                .bundleKey(event.getBundle() != null ? event.getBundle().getBundleKey() : null)
                .tsUtc(event.getTsUtc())
                .sourceType(event.getSourceType())
                .host(event.getHost())
                .userName(event.getUserName())
                .srcIp(event.getSrcIp())
                .dstIp(event.getDstIp())
                .action(event.getAction())
                .objectValue(event.getObjectValue())
                .result(event.getResult())
                .severity(event.getSeverity())
                .message(event.getMessage())
                .build();
    }
}