package com.example.kubernetesclientsandbox.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@NoArgsConstructor
@Slf4j
public class HelloController {

    @GetMapping
    public String hello() {
        log.info("Hello from kubernetes client sandbox!");
        return "Hello from kubernetes client sandbox!";
    }
}
