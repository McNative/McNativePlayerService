package org.mcnative.service.player;

import com.rabbitmq.client.ConnectionFactory;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.logging.PretronicLoggerFactory;
import net.pretronic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;
import org.mcnative.actionframework.sdk.actions.player.PlayerJoinAction;
import org.mcnative.actionframework.sdk.actions.player.PlayerLeaveAction;
import org.mcnative.actionframework.service.connector.rabbitmq.MAFRabbitMQConnector;
import org.mcnative.service.player.listener.PlayerJoinActionListener;
import org.mcnative.service.player.listener.PlayerLeaveActionListener;
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
}
