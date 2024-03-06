package com.example.kubernetesclientsandbox.service;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.dsl.ExecListener;

public class WatchListener implements ExecListener {
    @Override
    public void onClose(int code, String reason) {
        System.out.println("Execution completed...");
    }

    @Override
    public void onOpen() {
        ExecListener.super.onOpen();
    }

    @Override
    public void onFailure(Throwable t, Response failureResponse) {
        ExecListener.super.onFailure(t, failureResponse);
    }

    @Override
    public void onExit(int code, Status status) {
        ExecListener.super.onExit(code, status);
    }
}
