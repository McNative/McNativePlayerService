package org.mcnative.service.player;

public class GameProfile {

    private final String id;
    private final String name;
    private final String skinId;
    private final String capeId;

    public GameProfile(String id, String name, String skinId, String capeId) {
        this.id = id;
        this.name = name;
        this.skinId = skinId;
        this.capeId = capeId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkinId() {
        return skinId;
    }

    public String getCapeId() {
        return capeId;
    }
}
