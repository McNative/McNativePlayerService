package org.mcnative.service.player.listener;

import org.mcnative.actionframework.sdk.actions.player.PlayerLeaveAction;
import org.mcnative.actionframework.sdk.common.action.MAFActionExecutor;
import org.mcnative.actionframework.sdk.common.action.MAFActionListener;
import org.mcnative.service.player.McNativePlayerService;

import java.sql.Timestamp;

public class PlayerLeaveActionListener implements MAFActionListener<PlayerLeaveAction> {

    private final McNativePlayerService service;

    public PlayerLeaveActionListener(McNativePlayerService service) {
        this.service = service;
    }

    @Override
    public void onActionReceive(MAFActionExecutor executor, PlayerLeaveAction action) {
        long now = System.currentTimeMillis();
        Timestamp timestampNow = new Timestamp(now);

        this.service.getStorageService().getServerPlayersCollection().delete()
                .where("NetworkId", executor.getNetworkId().toString())
                .where("ServerId", executor.getClientId().toString())
                .where("PlayerId", action.getUniqueId())
                .execute();

        this.service.getStorageService().getNetworkPlayersCollection().update()
                .set("LastSeen", timestampNow)
                .where("NetworkId", executor.getNetworkId().toString())
                .where("PlayerId", action.getUniqueId().toString())
                .execute();

        this.service.getStorageService().getPlayersCollection().update()
                .set("LastSeen", timestampNow)
                .where("Id", action.getUniqueId().toString())
                .execute();

    }
}
