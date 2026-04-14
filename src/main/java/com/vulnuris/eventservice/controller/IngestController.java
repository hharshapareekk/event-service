package com.vulnuris.eventservice.controller;

import com.vulnuris.eventservice.dto.CesEventIngestDTO;
import com.vulnuris.eventservice.dto.IngestRequestDTO;
import com.vulnuris.eventservice.dto.IngestResponseDTO;
import com.vulnuris.eventservice.entity.Bundle;
import com.vulnuris.eventservice.entity.BundleMetadata;
import com.vulnuris.eventservice.entity.Event;
import com.vulnuris.eventservice.repository.BundleMetadataRepository;
import com.vulnuris.eventservice.repository.BundleRepository;
import com.vulnuris.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoint that simulates Kafka event consumption.
 * Accepts CES-formatted events and stores them as a new bundle.
 * When a real Kafka broker is available, a @KafkaListener can
 * call the same ingestEvents() logic.
 */
@RestController
@RequestMapping("/ingest")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IngestController {

    private final BundleRepository bundleRepository;
    private final BundleMetadataRepository bundleMetadataRepository;
    private final EventRepository eventRepository;

    @PostMapping
    public IngestResponseDTO ingest(@RequestBody IngestRequestDTO request) {
        List<CesEventIngestDTO> events = request.getEvents();
        if (events == null || events.isEmpty()) {
            return IngestResponseDTO.builder()
                    .eventCount(0)
                    .message("No events provided")
                    .build();
        }

        // Create a new bundle
        String bundleKey = "ingest_" + UUID.randomUUID().toString().substring(0, 8);
        Bundle bundle = bundleRepository.save(Bundle.builder()
                .bundleKey(bundleKey)
                .name(request.getBundleName() != null ? request.getBundleName() : "Ingested bundle")
                .description(request.getDescription() != null ? request.getDescription() : "Ingested via REST API")
                .createdAt(LocalDateTime.now())
                .build());

        bundleMetadataRepository.save(BundleMetadata.builder()
                .bundle(bundle)
                .payloadJson("{\"source\":\"rest_ingest\",\"eventCount\":" + events.size() + "}")
                .build());

        // Convert and save each event
        int saved = 0;
        for (CesEventIngestDTO dto : events) {
            try {
                Event event = mapCesToEvent(dto, bundle);
                eventRepository.save(event);
                saved++;
            } catch (Exception e) {
                // Skip malformed events, log in production
            }
        }

        return IngestResponseDTO.builder()
                .bundleId(bundle.getId())
                .bundleKey(bundleKey)
                .eventCount(saved)
                .message("Successfully ingested " + saved + " events into bundle #" + bundle.getId())
                .build();
    }

    private Event mapCesToEvent(CesEventIngestDTO dto, Bundle bundle) {
        LocalDateTime tsUtc = null;
        if (dto.getTsUtc() != null) {
            tsUtc = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(dto.getTsUtc().longValue(),
                            (long) ((dto.getTsUtc() % 1) * 1_000_000_000)),
                    ZoneOffset.UTC);
        }

        return Event.builder()
                .bundle(bundle)
                .tsUtc(tsUtc)
                .tsOriginal(dto.getTsOriginal())
                .tzOffset(dto.getTzOffset())
                .tsUncertaintyMs(dto.getTsUncertaintyMs())
                .sourceType(dto.getSourceType())
                .host(dto.getHost())
                .userName(dto.getUser())         // CES "user" → entity "userName"
                .srcIp(dto.getSrcIp())
                .srcPort(dto.getSrcPort())
                .dstIp(dto.getDstIp())
                .dstPort(dto.getDstPort())
                .protocol(dto.getProtocol())
                .action(dto.getAction())
                .objectValue(dto.getObject())    // CES "object" → entity "objectValue"
                .result(dto.getResult())
                .severity(dto.getSeverity())
                .message(dto.getMessage())
                .metadataJson(dto.getMetadataJson())
                .iocsJson(dto.getIocsJson())
                .correlationKeysJson(dto.getCorrelationKeysJson())
                .rawRefJson(dto.getRawRefJson())
                .build();
    }
}
