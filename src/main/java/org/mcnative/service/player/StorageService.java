package org.mcnative.service.player;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfigBuilder;
import net.pretronic.libraries.logging.PretronicLogger;
import org.mcnative.service.player.util.Environment;

import java.net.InetSocketAddress;
import java.util.UUID;

public class StorageService {

    private final DatabaseDriver databaseDriver;
    private final Database database;

    private final DatabaseCollection playersCollection;
    private final DatabaseCollection networkPlayersCollection;
    private final DatabaseCollection serverPlayersCollection;

    public StorageService(PretronicLogger logger) {
        DatabaseDriverConfig<?> storageConfiguration = new SQLDatabaseDriverConfigBuilder()
                .setAddress(InetSocketAddress.createUnresolved(Environment.getVariable("DATABASE_HOST"),
                        Integer.parseInt(Environment.getVariable("DATABASE_PORT"))))
                .setDialect(Dialect.byName(Environment.getVariable("DATABASE_DIALECT")))
                .setUsername(Environment.getVariable("DATABASE_USERNAME"))
                .setPassword(Environment.getVariable("DATABASE_PASSWORD"))
                .build();

        this.databaseDriver = DatabaseDriverFactory.create("McNativePlayerService", storageConfiguration, logger);
        this.databaseDriver.connect();

        this.database = databaseDriver.getDatabase(Environment.getVariable("DATABASE_NAME"));

        this.playersCollection = database.getCollection("mcnative_players");
        this.networkPlayersCollection = database.getCollection("mcnative_players_network");
        this.serverPlayersCollection = database.getCollection("mcnative_players_server");
    }

    public DatabaseCollection getPlayersCollection() {
        return playersCollection;
    }

    public DatabaseCollection getNetworkPlayersCollection() {
        return networkPlayersCollection;
    }

    public DatabaseCollection getServerPlayersCollection() {
        return serverPlayersCollection;
    }

    public QueryResultEntry getNetworkPlayer(UUID networkId, UUID playerId) {
        return getNetworkPlayersCollection().find()
                .where("NetworkId", networkId.toString())
                .where("PlayerId", playerId.toString())
                .execute().firstOrNull();
    }

    public QueryResultEntry getPlayer(UUID playerId) {
        return getPlayersCollection().find()
                .where("Id", playerId.toString())
                .execute().firstOrNull();
    }

    public QueryResultEntry getServerPlayer(UUID networkId, UUID serverId, UUID playerId) {
        return getServerPlayersCollection().find()
                .where("NetworkId", networkId.toString())
                .where("ServerId", serverId.toString())
                .where("PlayerId", playerId.toString())
                .execute().firstOrNull();
    }

    public void removeServerPlayer(UUID networkId, UUID serverId, UUID playerId) {
        getServerPlayersCollection().delete()
                .where("NetworkId", networkId.toString())
                .where("ServerId", serverId.toString())
                .where("PlayerId", playerId.toString())
                .execute();
    }
}