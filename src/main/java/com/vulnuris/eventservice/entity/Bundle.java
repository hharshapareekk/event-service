package com.vulnuris.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bundle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bundleKey;

    private String name;

    private String description;

    private LocalDateTime createdAt;
}
