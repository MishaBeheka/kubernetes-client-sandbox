package com.example.kubernetesclientsandbox.service;

import com.example.kubernetesclientsandbox.dto.PodInfoDto;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Slf4j
public class PodManager {

    public List<PodInfoDto> printPods() {
        Config config = new ConfigBuilder().withAutoConfigure().build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {
            return client.pods().list().getItems()
                    .stream()
                    .map(pod -> new PodInfoDto(pod.getMetadata().getName(), pod.getStatus().getPhase()))
                    .toList();
        }
    }

    public String createPod() {
        Config config = new ConfigBuilder().withAutoConfigure().build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {

            Pod pod = new PodBuilder()
                    .withNewMetadata()
                    .withName("test-pod")
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("test-pod-container")
                    .withImage("europe-west1-docker.pkg.dev/gcp-final-task-project/kubernetes-client-sandbox/sandbox-app:6fe4d86")
                    .addNewPort()
                    .withContainerPort(8080)
                    .endPort()
                    .endContainer()
                    .endSpec()
                    .build();
            pod = client.pods().inNamespace("default").create(pod);

            CompletableFuture<Pod> podRunningFuture = new CompletableFuture<>();



            client.pods().inNamespace("default").withName(pod.getMetadata().getName()).watch(new PodWatcher());

            Pod runningPod = podRunningFuture.get(); // Blocks until the Pod is running
            System.out.println("Pod is running now! Name: " + runningPod.getMetadata().getName());
            return pod.getMetadata().getName() + " " + pod.getStatus().getPhase();
        } catch (KubernetesClientException | InterruptedException | ExecutionException e) {
            log.error("Error occurred while creating pod", e);
            return "Error occurred while creating pod";
        }
    }
}
