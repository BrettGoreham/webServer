package model.user;

import java.util.List;

public enum Roles {
    ROLE_ADMIN,
    ROLE_USER;


    public static List<Roles> getDefaultRoles() {
        return List.of(ROLE_USER);
    }
}
