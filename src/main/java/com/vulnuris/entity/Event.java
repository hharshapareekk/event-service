package com.vulnuris.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

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

    // 🔗 Link to bundle
    @ManyToOne
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    private LocalDateTime tsUtc;

    private String sourceType;

    private String host;

    private String userName;

    private String srcIp;

    private String dstIp;

    private String action;

    private String objectValue;

    private String result;

    private Integer severity;

    private String message;

 private String metadata;
private String iocs;
private String correlationKeys;
private String rawRef;
}