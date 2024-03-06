package com.example.kubernetesclientsandbox.service;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PodWatcher implements Watcher<Pod>{

    private final CompletableFuture<Pod> podRunningFuture;
    @Override
    public void eventReceived(Action action, Pod resource) {
        if (resource.getStatus().getPhase().equals("Running")) {
            podRunningFuture.complete(resource); // Pod is in Running state, complete the future.
        }
    }

    @Override
    public void onClose(WatcherException cause) {
        if (!podRunningFuture.isDone()) {
            podRunningFuture.completeExceptionally(cause); // If the watcher is closed before the Pod is running, complete the future exceptionally.
        }
    }
}
