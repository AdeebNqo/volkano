package interfaces;

import java.util.List;

public interface IConfiguration {
    void setUsername(String username);
    String getUsername();
    void setPassword(String password);
    String getPassword();
    String getIPAddress();
    int getPort();
    boolean isDebugOn();
    List<String> getSupportedFeatures();
    String getDescription();
    String getEmail();
}
