package org.mcnative.service.player.listener;

import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import org.mcnative.actionframework.sdk.actions.player.PlayerJoinAction;
import org.mcnative.actionframework.sdk.common.action.MAFActionExecutor;
import org.mcnative.actionframework.sdk.common.action.MAFActionListener;
import org.mcnative.service.player.GameProfile;
import org.mcnative.service.player.McNativePlayerService;
import org.mcnative.service.player.MojangRequester;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerJoinActionListener implements MAFActionListener<PlayerJoinAction> {

    private final McNativePlayerService service;

    public PlayerJoinActionListener(McNativePlayerService service) {
        this.service = service;
    }

    @Override
    public void onActionReceive(MAFActionExecutor executor, PlayerJoinAction action) {
        this.service.logIncomingAction(executor, action);
        Timestamp timestampNow = new Timestamp(System.currentTimeMillis());

        UUID playerId = action.getUniqueId();
        QueryResultEntry playersResultEntry = this.service.getStorageService().getPlayer(playerId);
        if(playersResultEntry == null) {
            GameProfile profile = MojangRequester.getPlayerProfile(playerId.toString());
            if(profile == null) {
                System.err.println("Can't lookup player profile for " + playerId.toString());
                return;
            }
            this.service.getStorageService().getPlayersCollection().insert()
                    .set("Id", action.getUniqueId().toString())
                    .set("Name", profile.getName())
                    .set("SkinId", profile.getSkinId())
                    .set("CapeId", profile.getCapeId())
                    .set("Registered", timestampNow)
                    .set("LastSeen", timestampNow)
                    .set("LastMojangLookup", new Timestamp(System.currentTimeMillis()))
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

        QueryResultEntry serverPlayersResultEntry = this.service.getStorageService().getServerPlayer(executor.getNetworkId(), executor.getClientId(), action.getUniqueId());

        if(serverPlayersResultEntry != null) {
            this.service.getStorageService().removeServerPlayer(executor.getNetworkId(), executor.getClientId(), action.getUniqueId());
            this.service.getLogger().error(String.format("Server player (%s) was not unregistered before from %s@%s",
                    action.getUniqueId().toString(),
                    executor.getNetworkId().toString(),
                    executor.getClientId().toString()));
        }
        this.service.getStorageService().getServerPlayersCollection().insert()
                .set("NetworkId", executor.getNetworkId().toString())
                .set("ServerId", executor.getClientId().toString())
                .set("PlayerId", action.getUniqueId().toString())
                .set("Joined", timestampNow)
                .execute();
    }
}
