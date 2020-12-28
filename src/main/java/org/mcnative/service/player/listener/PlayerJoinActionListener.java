package org.mcnative.service.player.listener;

import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import org.mcnative.actionframework.sdk.actions.player.PlayerJoinAction;
import org.mcnative.actionframework.sdk.common.action.MAFActionExecutor;
import org.mcnative.actionframework.sdk.common.action.MAFActionListener;
import org.mcnative.service.player.McNativePlayerService;

import java.sql.Timestamp;

public class PlayerJoinActionListener implements MAFActionListener<PlayerJoinAction> {

    private final McNativePlayerService service;

    public PlayerJoinActionListener(McNativePlayerService service) {
        this.service = service;
    }

    @Override
    public void onActionReceive(MAFActionExecutor executor, PlayerJoinAction action) {
        long now = System.currentTimeMillis();
        Timestamp timestampNow = new Timestamp(now);

        QueryResultEntry playersResultEntry = this.service.getStorageService().getPlayer(action.getUniqueId());
        if(playersResultEntry == null) {
            this.service.getStorageService().getPlayersCollection().insert()
                    .set("Id", action.getUniqueId().toString())
                    .set("Name", "")
                    .set("GameProfile", "")
                    .set("Registered", now)
                    .set("LastSeen", now)
                    .execute();
        } else {
            this.service.getStorageService().getPlayersCollection().update()
                    .set("LastSeen", timestampNow)
                    .where("Id", action.getUniqueId().toString())
                    .execute();
        }

        QueryResultEntry networkPlayersResultEntry = this.service.getStorageService().getNetworkPlayer(executor.getNetworkId(), action.getUniqueId());
        if(networkPlayersResultEntry == null) {
            this.service.getStorageService().getNetworkPlayersCollection().insert()
                    .set("NetworkId", executor.getNetworkId().toString())
                    .set("PlayerId", action.getUniqueId().toString())
                    .set("Registered", timestampNow)
                    .set("LastSeen", timestampNow)
                    .execute();
        } else {
            this.service.getStorageService().getNetworkPlayersCollection().update()
                    .set("LastSeen", timestampNow)
                    .where("NetworkId", executor.getNetworkId().toString())
                    .where("PlayerId", action.getUniqueId().toString())
                    .execute();
        }

        this.service.getStorageService().getServerPlayersCollection().insert()
                .set("NetworkId", executor.getNetworkId().toString())
                .set("ServerId", executor.getClientId().toString())
                .set("PlayerId", action.getUniqueId().toString())
                .set("Joined", timestampNow)
                .execute();
    }
}
