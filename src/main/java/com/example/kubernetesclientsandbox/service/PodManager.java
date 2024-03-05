package com.example.kubernetesclientsandbox.service;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Slf4j
public class PodManager {

    public List<String> printPods() {
        Config config = new ConfigBuilder().withAutoConfigure().build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {

            return client.pods()
                    .inNamespace("default")
                    .list()
                    .getItems()
                    .stream()
                    .map(pod -> pod.getMetadata().getName())
                    .toList();
        }
    }
}
