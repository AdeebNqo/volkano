package models.settings;


import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("username")
    String username;
    @SerializedName("password")
    String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
