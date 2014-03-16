/**
 * CraftenLauncher is an alternative Launcher for Minecraft developed by Mojang.
 * Copyright (C) 2013  Johannes "redbeard" Busch, Sascha "saschb2b" Becker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Standard Authentication Service against Mojang.
 *
 * @author saschb2b
 */
package de.craften.craftenlauncher.logic.auth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.craften.craftenlauncher.logic.Logger;
import de.craften.craftenlauncher.logic.json.JSONReader;
import de.craften.craftenlauncher.logic.json.JSONWriter;

public class AuthenticationService {
    private String mResponse = "";
    private String mAccessToken = "";
    private String mClientToken = "";
    private String mProfileID = "";
    private boolean mValid = false;
    private LastLogin mLastLogin;

    public String getResponse() {
        return mResponse;
    }

    public String getSessionID(String username, String password) {
        getSSID(username, password);
        String sessionID = null;
        if (this.mResponse != null && this.mResponse != "") {
            setClientTokenFromResponse(this.mResponse);
            setProfileIDFromRequest(this.mResponse);
            sessionID = "token:" + getAccessToken() + ":" + getProfileID();
            Logger.getInstance().logInfo("SessionID created");

            LastLogin login = new LastLogin(username, getName(), getAccessToken(), getProfileID(), getClientToken());
            JSONWriter.saveLastLogin(login);
            Logger.getInstance().logInfo("Saved showProfile to lastlogin.json");
        } else {
            Logger.getInstance().logError("Login failed");
        }
        return sessionID;
    }

    public String getSessionID(LastLogin login) {
        this.mValid = isValid(login.getAccessToken());
        String sessionID = null;
        if (this.mValid) {
            this.mLastLogin = login;
            setClientToken(this.mLastLogin.getClientToken());
            setAccessToken(this.mLastLogin.getAccessToken());
            setProfileID(this.mLastLogin.getProfileID());
            sessionID = "token:" + login.getAccessToken() + ":" + login.getProfileID();
            JSONWriter.saveLastLogin(login);
            Logger.getInstance().logInfo("Login with LastLogin successful");
            return sessionID;
        } else {
            Logger.getInstance().logError("Login failed");
        }
        return sessionID;
    }

    public String getAccessToken() {
        if (this.mValid)
            return mLastLogin.getAccessToken();

        JsonParser parser = new JsonParser();
        Object obj = parser.parse(this.mResponse);
        JsonObject jsonObject = (JsonObject) obj;

        this.mAccessToken = jsonObject.get("accessToken").getAsString();
        return this.mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    public String getClientToken() {
        if (this.mValid)
            return mLastLogin.getClientToken();

        return mClientToken;
    }

    public void setClientToken(String value) {
        this.mClientToken = value;
    }

    public void setClientTokenFromResponse(String response) {
        JsonParser parser = new JsonParser();
        Object obj = parser.parse(response);
        JsonObject jsonObject = (JsonObject) obj;

        this.mClientToken = jsonObject.get("clientToken").getAsString();
    }

    private void setProfileIDFromRequest(String response) {
        JsonParser parser = new JsonParser();
        Object obj = parser.parse(this.mResponse);
        JsonObject jsonObject = (JsonObject) obj;

        JsonObject selectedProfile = jsonObject.get("selectedProfile").getAsJsonObject();
        this.mProfileID = selectedProfile.get("id").getAsString();
    }

    public void setProfileID(String profileID) {
        this.mProfileID = profileID;
    }

    public String getProfileID() {
        return mProfileID;
    }

    public String getName() {
        if (this.mValid)
            return mLastLogin.getUsername();

        JsonParser parser = new JsonParser();
        Object obj = parser.parse(this.mResponse);
        JsonObject jsonObject = (JsonObject) obj;

        JsonObject selectedProfile = jsonObject.get("selectedProfile").getAsJsonObject();
        String name = selectedProfile.get("name").getAsString();
        return name;
    }

    private String getSSID(String username, String password) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        JsonObject jsonResult = new JsonObject(), jsonNameVersion = new JsonObject();

        jsonNameVersion.addProperty("name", "Minecraft");
        jsonNameVersion.addProperty("version", 1);
        jsonResult.add("agent", jsonNameVersion);
        jsonResult.addProperty("username", username);
        jsonResult.addProperty("password", password);

        this.mResponse = executePost("https://authserver.mojang.com/authenticate", gson.toJson(jsonResult));

        return this.mResponse;
    }

    public static boolean isValid(String accessToken) {
        JsonObject jsonAccessToken = new JsonObject();
        jsonAccessToken.addProperty("accessToken", accessToken);

        String dummy = executePost("https://authserver.mojang.com/validate", jsonAccessToken.toString());
        return dummy != null;
    }

    public static String executePost(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            byte[] bytes = urlParameters.getBytes("UTF-8");
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json; charset=utf-8");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(bytes.length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            System.out.println(connection.getURL());
            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.write(bytes);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

        	Logger.getInstance().logError("AuthSer->executePost error: " + e.getMessage());
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String genUUID() {
        return UUID.randomUUID().toString();
    }

    public LastLogin readLastLogin(String minecraftDir) {
        return JSONReader.readLastLogin(minecraftDir);
    }

    public void deleteLastLogin(String minecraftDir) {
        Logger.getInstance().logInfo("Trying to delete lastLogin! (At: " + minecraftDir + " )");

        String path;

        if (minecraftDir.endsWith(File.separator)) {
            path = minecraftDir + "lastLogin.json";
        } else {
            path = minecraftDir + File.separator + "lastLogin.json";
        }

        File lastLogin = new File(path);

        if (lastLogin.exists()) {
            try {
                lastLogin.delete();
                Logger.getInstance().logInfo("LastLogin at: " + path + " deleted!");
            } catch (Exception e) {
                Logger.getInstance().logError("Could not delete LastLogin at: " + path);
            }

        }
    }
}