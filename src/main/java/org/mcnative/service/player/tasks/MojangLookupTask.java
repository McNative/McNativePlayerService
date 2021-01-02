package org.mcnative.service.player.tasks;

import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import org.mcnative.service.player.GameProfile;
import org.mcnative.service.player.McNativePlayerService;
import org.mcnative.service.player.MojangRequester;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class MojangLookupTask extends Thread {

    private final McNativePlayerService service;

    public MojangLookupTask(McNativePlayerService service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println("Starting Mojang lookup task.");
            int page = 1;
            QueryResult result;
            while (!(result = this.service.getStorageService().getPlayersCollection().find().page(page, 1000).execute()).isEmpty()) {
                for (QueryResultEntry resultEntry : result) {
                    long lastMojangLookup = ((Timestamp)resultEntry.getObject("LastMojangLookup")).getTime();
                    boolean lookup = (System.currentTimeMillis()-lastMojangLookup) >= TimeUnit.HOURS.toMillis(1);

                    if(lookup) {
                        String playerId = resultEntry.getString("Id");
                        GameProfile profile = MojangRequester.getPlayerProfile(playerId);
                        if(profile == null) {
                            System.out.println("Can't lookup player profile for " + playerId);
                            continue;
                        }
                        this.service.getStorageService().getPlayersCollection().update()
                                .set("Name", profile.getName())
                                .set("SkinId", profile.getSkinId())
                                .set("CapeId", profile.getCapeId())
                                .set("LastMojangLookup", new Timestamp(System.currentTimeMillis()))
                                .where("Id", playerId)
                                .execute();
                    }
                }
                page++;
            }
            System.out.println("Finished mojang lookup. Next lookup in 30 minutes.");
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(30));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
