package models;

import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("hub_connect")
    HubConnect hubConnectDetails;

    @SerializedName("user_details")
    UserDetails userDetails;

    public HubConnect getHubConnectDetails() {
        return hubConnectDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}
