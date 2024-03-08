package com.example.kubernetesclientsandbox.controller;

import com.example.kubernetesclientsandbox.service.PodManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping ("/create")
    public ResponseEntity<?> createdPod(@RequestBody String podName) {
        return ResponseEntity.ok(podManager.createPod(podName));
    }

    @PatchMapping ("/{podName}")
    public ResponseEntity<?> createdPod(@PathVariable String podName, @RequestBody String command) {
        return ResponseEntity.ok(podManager.executeCommandInPod(podName, command));
    }

    @GetMapping ("/{podName}/logs")
    public ResponseEntity<?> getPodLogs(@PathVariable String podName) {
        return ResponseEntity.ok(podManager.getPodLogs(podName));
    }
}
