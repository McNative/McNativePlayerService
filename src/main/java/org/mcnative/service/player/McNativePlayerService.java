package org.mcnative.service.player;

import com.rabbitmq.client.ConnectionFactory;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.logging.PretronicLogger;
import org.mcnative.actionframework.sdk.actions.player.PlayerJoinAction;
import org.mcnative.actionframework.sdk.actions.player.PlayerLeaveAction;
import org.mcnative.actionframework.sdk.actions.server.ServerShutdownConfirmAction;
import org.mcnative.actionframework.sdk.common.action.MAFAction;
import org.mcnative.actionframework.sdk.common.action.MAFActionExecutor;
import org.mcnative.actionframework.service.connector.rabbitmq.MAFRabbitMQConnector;
import org.mcnative.service.player.listener.PlayerJoinActionListener;
import org.mcnative.service.player.listener.PlayerLeaveActionListener;
import org.mcnative.service.player.listener.ServerShutdownConfirmActionListener;
import org.mcnative.service.player.util.Environment;

public final class McNativePlayerService {

    private final MAFRabbitMQConnector mafConnector;
    private final StorageService storageService;
    private final PretronicLogger logger;

    public McNativePlayerService(PretronicLogger logger) {
        this.logger = logger;

        this.storageService = new StorageService(logger);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Environment.getVariable("RABBITMQ_HOST"));
        factory.setUsername(Environment.getVariable("RABBITMQ_USERNAME"));
        factory.setPassword(Environment.getVariable("RABBITMQ_PASSWORD"));
        this.mafConnector = MAFRabbitMQConnector.createShared(factory, "McNativeMonitoringService", true);
        registerActionListeners();
        this.mafConnector.connect();
    }

    private void registerActionListeners() {
        this.mafConnector.subscribeAction(PlayerJoinAction.class, new PlayerJoinActionListener(this));
        this.mafConnector.subscribeAction(PlayerLeaveAction.class, new PlayerLeaveActionListener(this));
        this.mafConnector.subscribeAction(ServerShutdownConfirmAction.class, new ServerShutdownConfirmActionListener(this));
    }

    protected void stop() {
        this.mafConnector.disconnect();
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public PretronicLogger getLogger() {
        return logger;
    }

    public void logIncomingAction(MAFActionExecutor executor, MAFAction action) {
        getLogger().info("Received "+action.getNamespace()+"@"+action.getName()+" from "
                + executor.getNetworkId().toString() + "@" + executor.getClientId().toString() + " " +
                DocumentFileType.JSON.getWriter().write(Document.newDocument(action), false));
    }
}
