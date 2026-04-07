package com.vulnuris.eventservice.service;

import com.vulnuris.eventservice.dto.EventRequestDTO;
import com.vulnuris.eventservice.dto.EventResponseDTO;
import com.vulnuris.eventservice.dto.TimelineResponseDTO;
import com.vulnuris.eventservice.entity.Bundle;
import com.vulnuris.eventservice.entity.Event;
import com.vulnuris.eventservice.repository.BundleRepository;
import com.vulnuris.eventservice.repository.EventRepository;
import com.vulnuris.eventservice.spec.EventSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final BundleRepository bundleRepository;

    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO dto) {
        Bundle bundle = bundleRepository.findById(dto.getBundleId())
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found: " + dto.getBundleId()));

        Event event = Event.builder()
                .bundle(bundle)
                .tsUtc(dto.getTsUtc())
                .tsOriginal(dto.getTsOriginal())
                .tzOffset(dto.getTzOffset())
                .tsUncertaintyMs(dto.getTsUncertaintyMs())
                .sourceType(dto.getSourceType())
                .host(dto.getHost())
                .userName(dto.getUserName())
                .srcIp(dto.getSrcIp())
                .srcPort(dto.getSrcPort())
                .dstIp(dto.getDstIp())
                .dstPort(dto.getDstPort())
                .protocol(dto.getProtocol())
                .action(dto.getAction())
                .objectValue(dto.getObjectValue())
                .result(dto.getResult())
                .severity(dto.getSeverity())
                .message(dto.getMessage())
                .metadataJson(dto.getMetadata())
                .iocsJson(dto.getIocs())
                .correlationKeysJson(dto.getCorrelationKeys())
                .rawRefJson(dto.getRawRef())
                .build();

        return mapToResponse(eventRepository.save(event));
    }

    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "tsUtc")).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
        return mapToResponse(event);
    }

    public List<EventResponseDTO> getEventsByBundle(Long bundleId) {
        return eventRepository.findByBundle_Id(bundleId).stream()
                .sorted(Comparator.comparing(Event::getTsUtc, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Timeline: all sources in one bundle, ordered by {@code tsUtc}.
     * This is the unified story line; the correlation service can later add graph edges separately.
     */
    public TimelineResponseDTO getTimeline(Long bundleId, LocalDateTime fromUtc, LocalDateTime toUtc) {
        Bundle bundle = bundleRepository.findById(bundleId)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found: " + bundleId));

        List<Event> events;
        if (fromUtc != null && toUtc != null) {
            events = eventRepository.findByBundle_IdAndTsUtcBetween(bundleId, fromUtc, toUtc);
        } else {
            events = eventRepository.findByBundle_Id(bundleId);
        }
        events.sort(Comparator.comparing(Event::getTsUtc, Comparator.nullsLast(Comparator.naturalOrder())));

        List<EventResponseDTO> dtos = events.stream().map(this::mapToResponse).collect(Collectors.toList());

        LocalDateTime rangeStart = dtos.stream().map(EventResponseDTO::getTsUtc).min(Comparator.naturalOrder()).orElse(null);
        LocalDateTime rangeEnd = dtos.stream().map(EventResponseDTO::getTsUtc).max(Comparator.naturalOrder()).orElse(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("count", dtos.size());
        stats.put("sources", dtos.stream().map(EventResponseDTO::getSourceType).distinct().sorted().collect(Collectors.toList()));

        return TimelineResponseDTO.builder()
                .bundleId(bundle.getId())
                .bundleKey(bundle.getBundleKey())
                .events(dtos)
                .stats(stats)
                .rangeStartUtc(rangeStart)
                .rangeEndUtc(rangeEnd)
                .build();
    }

    public List<EventResponseDTO> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByTsUtcBetween(start, end).stream()
                .sorted(Comparator.comparing(Event::getTsUtc))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search / filter API — supports single bundle or global listing with optional text query on message/action/object.
     */
    public List<EventResponseDTO> search(
            Long bundleId,
            String sourceType,
            String userName,
            String host,
            String srcIp,
            Integer minSeverity,
            Integer maxSeverity,
            LocalDateTime fromUtc,
            LocalDateTime toUtc,
            String q
    ) {
        Specification<Event> spec = EventSpecifications.search(
                bundleId, sourceType, userName, host, srcIp, minSeverity, maxSeverity, fromUtc, toUtc, q);
        return eventRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "tsUtc")).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EventResponseDTO mapToResponse(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .bundleId(event.getBundle() != null ? event.getBundle().getId() : null)
                .bundleKey(event.getBundle() != null ? event.getBundle().getBundleKey() : null)
                .tsUtc(event.getTsUtc())
                .tsOriginal(event.getTsOriginal())
                .tzOffset(event.getTzOffset())
                .tsUncertaintyMs(event.getTsUncertaintyMs())
                .sourceType(event.getSourceType())
                .host(event.getHost())
                .userName(event.getUserName())
                .srcIp(event.getSrcIp())
                .srcPort(event.getSrcPort())
                .dstIp(event.getDstIp())
                .dstPort(event.getDstPort())
                .protocol(event.getProtocol())
                .action(event.getAction())
                .objectValue(event.getObjectValue())
                .result(event.getResult())
                .severity(event.getSeverity())
                .message(event.getMessage())
                .metadata(event.getMetadataJson())
                .iocs(event.getIocsJson())
                .correlationKeys(event.getCorrelationKeysJson())
                .rawRef(event.getRawRefJson())
                .build();
    }
}
