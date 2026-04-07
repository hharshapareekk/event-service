package com.vulnuris.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Optional ingestion/source metadata per bundle (timezone hints, file list, etc.).
 * Table name matches project spec "metadata" — stored as {@code bundle_metadata} in PostgreSQL.
 */
@Entity
@Table(name = "bundle_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "bundle_id", unique = true)
    private Bundle bundle;

    /** JSON: e.g. { "timezone_hint": "Asia/Kolkata", "files": ["email.jsonl"] } */
    @Column(columnDefinition = "text")
    private String payloadJson;
}
