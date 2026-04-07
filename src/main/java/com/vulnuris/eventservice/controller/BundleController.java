package com.vulnuris.eventservice.controller;

import com.vulnuris.eventservice.entity.Bundle;
import com.vulnuris.eventservice.repository.BundleRepository;
import com.vulnuris.eventservice.service.BundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bundles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BundleController {

    private final BundleService bundleService;
    private final BundleRepository bundleRepository;

    @PostMapping
    public Bundle create(@RequestBody Bundle bundle) {
        return bundleService.createBundle(bundle);
    }

    @GetMapping
    public List<Bundle> list() {
        return bundleRepository.findAll();
    }

    @GetMapping("/{id}")
    public Bundle get(@PathVariable Long id) {
        return bundleService.getBundle(id);
    }
}
