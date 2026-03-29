package com.vulnuris.eventservice.repository;

import com.vulnuris.eventservice.entity.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Optional<Bundle> findByBundleKey(String bundleKey);
}