package org.mcnative.service.player.listener;

import org.mcnative.actionframework.sdk.actions.server.ServerRecoveryAction;
import org.mcnative.actionframework.sdk.common.action.MAFActionExecutor;
import org.mcnative.actionframework.sdk.common.action.MAFActionListener;
import org.mcnative.service.player.McNativePlayerService;

import java.util.Map;
import java.util.UUID;

public class ServerRecoveryActionListener implements MAFActionListener<ServerRecoveryAction> {

    private final McNativePlayerService service;

    public ServerRecoveryActionListener(McNativePlayerService service) {
        this.service = service;
    }

    @Override
    public void onActionReceive(MAFActionExecutor executor, ServerRecoveryAction action) {
        this.service.getStorageService().getServerPlayersCollection().delete()
                .where("NetworkId", executor.getNetworkId().toString())
                .where("ServerId", executor.getClientId().toString())
                .execute();
        for (Map.Entry<UUID, Integer> entry : action.getOnlinePlayers().entrySet()) {
            this.service.getStorageService().handlePlayerRegister(executor.getNetworkId(), executor.getClientId(),
                    entry.getKey(), entry.getValue());
        }
    }
}
