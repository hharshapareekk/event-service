package com.vulnuris.eventservice.repository;

import com.vulnuris.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByBundle_Id(Long bundleId);

    List<Event> findByBundle_IdAndTsUtcBetween(Long bundleId, LocalDateTime start, LocalDateTime end);

    List<Event> findByTsUtcBetween(LocalDateTime start, LocalDateTime end);
}
