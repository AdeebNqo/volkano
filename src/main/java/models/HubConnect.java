package models;

import com.google.gson.annotations.SerializedName;

public class HubConnect {

    @SerializedName("address")
    String address;
    @SerializedName("port")
    Long port;

    public String getAddress() {
        return address;
    }

    public Long getPort() {
        return port;
    }
}
