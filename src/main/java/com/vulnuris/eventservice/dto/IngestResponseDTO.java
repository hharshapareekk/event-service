package com.vulnuris.eventservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestResponseDTO {

    private Long bundleId;
    private String bundleKey;
    private int eventCount;
    private String message;
}
