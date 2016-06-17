package models.settings;


import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("username")
    String username;
    @SerializedName("password")
    String password;
    @SerializedName("description")
    String description;
    @SerializedName("email")
    String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }
}
