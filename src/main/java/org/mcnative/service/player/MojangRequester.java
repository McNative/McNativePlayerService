package org.mcnative.service.player;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.http.HttpClient;
import net.pretronic.libraries.utility.http.HttpResult;
import net.pretronic.libraries.utility.map.Pair;

import java.util.Base64;

public class MojangRequester {

    private static final String PLAYER_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    public static GameProfile getPlayerProfile(String playerId) {
        Validate.notNull(playerId);
        HttpClient client = new HttpClient();
        client.setUrl(String.format(PLAYER_PROFILE, playerId));

        HttpResult result = client.connect();

        Document profile = DocumentFileType.JSON.getReader().read(result.getContent());

        Document properties = profile.getDocument("properties");

        Document textures = null;
        for (DocumentEntry property : properties) {
            if(property.toDocument().getString("name").equals("textures")) textures = (Document) property;
        }
        if(textures != null) {
            profile = DocumentFileType.JSON.getReader().read(Base64.getDecoder().decode(textures.getString("value")));

            String name = profile.getString("profileName");

            textures = profile.getDocument("textures");

            Document skin = textures.getDocument("SKIN");
            Document cape = textures.getDocument("CAPE");

            String skinId = null;
            String capeId = null;

            if(skin != null) {
                String url = skin.getString("url");
                skinId = url.substring(url.lastIndexOf("/")+1);
            }
            if(cape != null) {
                String url = cape.getString("url");
                capeId = url.substring(url.lastIndexOf("/")+1);

            }
            return new GameProfile(playerId, name, skinId, capeId);
        }
        return null;
    }
}
