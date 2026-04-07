package com.vulnuris.eventservice.repository;

import com.vulnuris.eventservice.entity.BundleMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleMetadataRepository extends JpaRepository<BundleMetadata, Long> {

    Optional<BundleMetadata> findByBundle_Id(Long bundleId);
}
