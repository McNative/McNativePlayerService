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
        this.service.getStorageService().handlePlayerRegister(executor.getNetworkId(), executor.getClientId(), action.getUniqueId(), action.getProtocolVersion());
    }
}
