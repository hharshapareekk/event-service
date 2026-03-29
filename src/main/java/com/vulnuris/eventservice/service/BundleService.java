package com.vulnuris.eventservice.service;

import com.vulnuris.eventservice.entity.Bundle;
import com.vulnuris.eventservice.repository.BundleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BundleService {

    private final BundleRepository bundleRepository;

    public Bundle createBundle(Bundle bundle) {
        bundle.setCreatedAt(LocalDateTime.now());
        return bundleRepository.save(bundle);
    }

    public Bundle getBundle(Long id) {
        return bundleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));
    }
}