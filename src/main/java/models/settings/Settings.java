package models.settings;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Settings {

    @SerializedName("hub_connect")
    HubConnect hubConnectDetails;

    @SerializedName("user_details")
    UserDetails userDetails;

    @SerializedName("client_supports")
    List<String> supportedFeatures;

    public HubConnect getHubConnectDetails() {
        return hubConnectDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public List<String> getSupportedFeatures() {
        return supportedFeatures;
    }
}
