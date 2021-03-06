package Models;

public class UserModel {

    String UID;
    String email;
    String username;
    String profileUrl;

    public UserModel()
    {
    }

    public UserModel(String UID, String email, String username, String profileUrl) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    public UserModel(String UID, String email, String username) {
        this.UID = UID;
        this.email = email;
        this.username = username;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
