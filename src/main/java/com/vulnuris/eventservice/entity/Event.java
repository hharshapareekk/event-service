package com.vulnuris.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    /** Normalized time in UTC (CES @ts_utc) */
    private LocalDateTime tsUtc;

    private String tsOriginal;
    /** e.g. "+05:30" or minutes as string */
    private String tzOffset;
    private Integer tsUncertaintyMs;

    private String sourceType;
    private String host;
    private String userName;
    private String srcIp;
    private Integer srcPort;
    private String dstIp;
    private Integer dstPort;
    private String protocol;

    private String action;
    private String objectValue;
    private String result;
    private Integer severity;
    private String message;

    /** JSON strings for flexible CES fields */
    private String metadataJson;
    private String iocsJson;
    private String correlationKeysJson;
    private String rawRefJson;
}
