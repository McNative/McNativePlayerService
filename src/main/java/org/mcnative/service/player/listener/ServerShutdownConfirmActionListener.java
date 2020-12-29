package org.mcnative.service.player.listener;

import org.mcnative.actionframework.sdk.actions.server.ServerShutdownConfirmAction;
import org.mcnative.actionframework.sdk.common.action.MAFActionExecutor;
import org.mcnative.actionframework.sdk.common.action.MAFActionListener;
import org.mcnative.service.player.McNativePlayerService;

public class ServerShutdownConfirmActionListener implements MAFActionListener<ServerShutdownConfirmAction> {

    private final McNativePlayerService service;

    public ServerShutdownConfirmActionListener(McNativePlayerService service) {
        this.service = service;
    }

    @Override
    public void onActionReceive(MAFActionExecutor executor, ServerShutdownConfirmAction action) {
        this.service.logIncomingAction(executor, action);
        this.service.getStorageService().getServerPlayersCollection().delete()
                .where("NetworkId", executor.getNetworkId().toString())
                .where("ServerId", executor.getClientId().toString())
                .execute();
    }
}
