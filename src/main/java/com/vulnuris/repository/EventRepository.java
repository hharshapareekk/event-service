package com.vulnuris.eventservice.repository;

import com.vulnuris.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByBundleId(Long bundleId);

    List<Event> findByTsUtcBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findBySourceType(String sourceType);

    List<Event> findByUserName(String userName);
}