package model.user;

import java.util.List;

public class SimpleUser {
    private long id;
    private String username;
    private List<Roles> roles;

    public SimpleUser(long id, String username, List<Roles> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
