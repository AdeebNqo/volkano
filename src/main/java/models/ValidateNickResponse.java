package models;

public class ValidateNickResponse {
    private boolean isNickValid = true;
    private String actualResponse;
    private boolean isNickRegistered;

    public ValidateNickResponse(boolean isNickValid, boolean isNickRegistered,String actualResponse) {
        this.isNickValid = isNickValid;
        this.actualResponse = actualResponse;
        this.isNickRegistered = isNickRegistered;
    }

    public boolean isNickValid() {
        return isNickValid;
    }
    public String getResponse() {
        return actualResponse;
    }

    public boolean isNickRegistered() {
        return isNickRegistered;
    }
}
