package net.glowstone.entity.meta.profile;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class PlayerDataFetcher {

    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String PROFILE_URL_PREFIX = "?unsigned=false";

    private static final String UUID_URL = "https://api.mojang.com/profiles/minecraft";

    public static PlayerProfile getProfile(UUID uuid) {
        URL url = new URL(PROFILE_URL + uuid + PROFILE_URL_PREFIX);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        try {
            JSONObject json = new JSONParser().parse(new InputStreamReader(is));
        } catch (ParseException e) {
            GlowServer.logger.warning("Couldn't get profile for UUID " + uuid);
            return;
        }

        PlayerProfile profile = PlayerProfile.parseProfile(json);
        return profile;
    }

    public static UUID getUUID(String playerName) {
        URL url = new URL(UUID_URL);
        HTTPSURLConnection conn = (HTTPSUrlConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        List<String> playerList = new ArrayList<String>();
        playerList.add(playerName);

        OutputStream os = conn.getOutputStream();
        os.write(new JSONArray(playerList.toArray()));
        os.flush();
        os.close();


        InputStream is = conn.getInputStream();
        try {
            JSONObject json = new JSONParser().parse(new InputStreamReader(is));
        } catch (ParseException e) {
            GlowServer.logger.warning("Couldn't get profile for UUID " + uuid);
            return;
        }

        return parseUUIDResponse(json);
    }

    public static UUID parseUUIDResponse(JSONObject json) {
        JSONArray uuids = (JSONArray) json;
        String uuid = uuids.get(0).get("id");
        return UuidUtils.fromFlatString(uuid);    
    }
}
