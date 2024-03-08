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
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
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

    public String createPod(String podName) {
        Config config = new ConfigBuilder().withAutoConfigure().build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {

            Pod pod = new PodBuilder()
                    .withNewMetadata()
                    .withName(podName)
                    .endMetadata()
                    .withNewSpec()
                    .withActiveDeadlineSeconds(600L)
                    .addNewContainer()
                    .withName(podName + "-container")
                    .withImage("europe-west1-docker.pkg.dev/gcp-final-task-project/build-tools/maven-17:v.0.0")
                    .addNewPort()
                    .withContainerPort(8080)
                    .endPort()
                    .endContainer()
                    .endSpec()
                    .build();

            log.info("Creating pod: {} ", pod.getMetadata().getName());
            pod = client.pods().inNamespace("default").resource(pod).create();

            CompletableFuture<Pod> podRunningFuture = new CompletableFuture<>();

            client.pods()
                    .inNamespace("default")
                    .withName(pod.getMetadata().getName())
                    .watch(new PodWatcher(podRunningFuture));

            Pod runningPod = podRunningFuture.get(); // Blocks until the Pod is running
            System.out.println("Pod is running now! Name: " + runningPod.getMetadata().getName());

//            //execute command in pod
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            ByteArrayOutputStream error = new ByteArrayOutputStream();
//            try {
//                String[] command = {"/bin/sh", "-c", "ls /"};
//                ExecWatch execWatch = client.pods().inNamespace("default").withName(runningPod.getMetadata().getName())
//                        .writingOutput(output)
//                        .writingError(error)
//                        .usingListener(new WatchListener())
//                        .exec(command);
//
//                var exitCode = execWatch.exitCode().get(); // Blocks until the Pod is executing
//                log.info("Exit code: {}", exitCode);
//
//                execWatch.close();
//                log.info("Output: {}", output);
//                log.info("Error: {}", error);
//            } catch (Exception e) {
//                log.error("Error occurred while executing command in pod", e);
//                throw e;
//            }


            return runningPod.getMetadata().getName();
        } catch (KubernetesClientException | InterruptedException | ExecutionException e) {
            log.error("Error occurred while processing pod", e);
            return "Error occurred while processing pod";
        }
    }

    public String executeCommandInPod(String podName, String command) {
        Config config = new ConfigBuilder().withAutoConfigure().build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream error = new ByteArrayOutputStream();

            try (ExecWatch execWatch = client.pods()
                    .inNamespace("default")
                    .withName(podName)
                    .writingOutput(output)
                    .writingError(error)
                    .usingListener(new WatchListener())
                    .exec(command.split(" "))) {

                var exitCode = execWatch.exitCode().get(); // Blocks until the Pod is executing
                log.info("Exit code: {}", exitCode);

                return Optional.of(output)
                        .map(ByteArrayOutputStream::toString)
                        .filter(outputString -> !outputString.isEmpty() && !outputString.isBlank())
                        .orElseGet(() -> Optional.of(error)
                                .map(ByteArrayOutputStream::toString)
                                .filter(outputString -> !outputString.isEmpty() && !outputString.isBlank())
                                .orElse("No output"));
            } catch (KubernetesClientException | InterruptedException | ExecutionException e) {
                log.error("Error occurred while executing command in pod", e);
                return e.getMessage();
            }
        }
    }
}
