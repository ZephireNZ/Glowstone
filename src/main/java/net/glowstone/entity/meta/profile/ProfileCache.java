package net.glowstone.entity.meta.profile;

import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.meta.profile.PlayerDataFetcher;
import net.glowstone.entity.meta.profile.PlayerProperty;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class ProfileCache {
    private static Map<String, UUID> uuidCache = new HashMap<String, UUID>();
    private static Map<UUID, PlayerProfile> profileCache = new HashMap<UUID, PlayerProfile>();

    public static UUID getUUID(String playerName) {
        if (uuidCache.containsKey(playerName)) {
            return uuidCache.get(playerName);
        }
        uuidCache.put(playerName, PlayerDataFetcher.getUUID(playerName));
        return uuidCache.get(playerName);
    }

    public static PlayerProfile getProfile(UUID playerUUID) {
        if (profileCache.containsKey(playerUUID)) {
            return profileCache.get(playerUUID);
        }
        profileCache.put(playerUUID, PlayerDataFetcher.getProfile(playerUUID));
        return profileCache.get(playerUUID);
    }
}
