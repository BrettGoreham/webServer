package webServer.userManagement;

import database.UserDao;
import model.user.Roles;
import model.user.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UserService {
    @Autowired
    private UserDao userDao;

    public List<SimpleUser> GetUserList() {
        return userDao.getUserList();
    }

    public void GiveUserRole(int userId, Roles role) {
        SimpleUser user = userDao.getUserById(userId);

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);

            userDao.SetRoleListForUser(userId, user.getRoles());
        }

    }

    public void TakeUserRole(int userId, Roles role) {
        SimpleUser user = userDao.getUserById(userId);

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);

            userDao.SetRoleListForUser(userId, user.getRoles());
        }

    }
}
