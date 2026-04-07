package com.vulnuris.eventservice.service;

import com.vulnuris.eventservice.entity.Bundle;
import com.vulnuris.eventservice.entity.BundleMetadata;
import com.vulnuris.eventservice.repository.BundleMetadataRepository;
import com.vulnuris.eventservice.repository.BundleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BundleService {

    private final BundleRepository bundleRepository;
    private final BundleMetadataRepository bundleMetadataRepository;

    @Transactional
    public Bundle createBundle(Bundle bundle) {
        bundle.setCreatedAt(LocalDateTime.now());
        return bundleRepository.save(bundle);
    }

    public Bundle getBundle(Long id) {
        return bundleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found: " + id));
    }

    @Transactional
    public BundleMetadata saveMetadata(Long bundleId, String payloadJson) {
        Bundle bundle = getBundle(bundleId);
        return bundleMetadataRepository.findByBundle_Id(bundleId)
                .map(existing -> {
                    existing.setPayloadJson(payloadJson);
                    return bundleMetadataRepository.save(existing);
                })
                .orElseGet(() -> bundleMetadataRepository.save(BundleMetadata.builder()
                        .bundle(bundle)
                        .payloadJson(payloadJson)
                        .build()));
    }
}
