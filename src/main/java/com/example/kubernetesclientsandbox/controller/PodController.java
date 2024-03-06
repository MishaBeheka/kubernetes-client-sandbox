package com.example.kubernetesclientsandbox.controller;

import com.example.kubernetesclientsandbox.service.PodManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/pod")
@RequiredArgsConstructor
public class PodController {

    private final PodManager podManager;

    @GetMapping
    public ResponseEntity<?> getPods() {
        return ResponseEntity.ok(podManager.printPods());
    }

    @GetMapping("/create")
    public ResponseEntity<?> getCreatedPod() {
        return ResponseEntity.ok(podManager.createPod());
    }

}
