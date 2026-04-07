package com.vulnuris.eventservice.spec;

import com.vulnuris.eventservice.entity.Event;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class EventSpecifications {

    private EventSpecifications() {}

    public static Specification<Event> search(
            Long bundleId,
            String sourceType,
            String userName,
            String host,
            String srcIp,
            Integer minSeverity,
            Integer maxSeverity,
            LocalDateTime fromUtc,
            LocalDateTime toUtc,
            String q
    ) {
        return (root, query, cb) -> {
            List<Predicate> parts = new ArrayList<>();
            if (bundleId != null) {
                parts.add(cb.equal(root.get("bundle").get("id"), bundleId));
            }
            if (sourceType != null && !sourceType.isBlank()) {
                parts.add(cb.equal(cb.lower(root.get("sourceType")), sourceType.trim().toLowerCase()));
            }
            if (userName != null && !userName.isBlank()) {
                parts.add(cb.like(cb.lower(root.get("userName")), "%" + userName.trim().toLowerCase() + "%"));
            }
            if (host != null && !host.isBlank()) {
                parts.add(cb.like(cb.lower(root.get("host")), "%" + host.trim().toLowerCase() + "%"));
            }
            if (srcIp != null && !srcIp.isBlank()) {
                parts.add(cb.equal(root.get("srcIp"), srcIp.trim()));
            }
            if (minSeverity != null) {
                parts.add(cb.ge(root.get("severity"), minSeverity));
            }
            if (maxSeverity != null) {
                parts.add(cb.le(root.get("severity"), maxSeverity));
            }
            if (fromUtc != null) {
                parts.add(cb.greaterThanOrEqualTo(root.get("tsUtc"), fromUtc));
            }
            if (toUtc != null) {
                parts.add(cb.lessThanOrEqualTo(root.get("tsUtc"), toUtc));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                Predicate msg = cb.like(cb.lower(root.get("message")), pattern);
                Predicate act = cb.like(cb.lower(root.get("action")), pattern);
                Predicate obj = cb.like(cb.lower(root.get("objectValue")), pattern);
                parts.add(cb.or(msg, act, obj));
            }
            if (parts.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(parts.toArray(Predicate[]::new));
        };
    }
}
