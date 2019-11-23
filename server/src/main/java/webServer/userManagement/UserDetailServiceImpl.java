package webServer.userManagement;

import database.UserDao;
import model.user.Roles;
import model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUserDetails user = new SecurityUserDetails(userDao.getUserByUsername(username));

        return user;
    }

    public void createAndSaveUser(String username, String password, String email) {

        String encryptedPass = passwordEncoder.encode(password);
        User user = new User(username, encryptedPass, email);
        user.setRoles(Roles.getDefaultRoles());

        userDao.createUser(user);
    }
}
