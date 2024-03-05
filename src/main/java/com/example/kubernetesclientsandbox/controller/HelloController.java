package com.example.kubernetesclientsandbox.controller;

import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@NoArgsConstructor
public class HelloController {

    @GetMapping
    public String hello() {
        return "Hello from kubernetes client sandbox!";
    }
}
