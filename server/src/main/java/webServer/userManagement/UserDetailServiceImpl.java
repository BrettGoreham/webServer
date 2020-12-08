package webServer.userManagement;

import database.UserDao;
import model.user.Roles;
import model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import webServer.exceptions.InvalidInputException;
import webServer.scheduledTasks.ScheduledEmails;


@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ScheduledEmails scheduledEmails;

    @Value("baseUrl")
    private String baseUrl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUserDetails user = new SecurityUserDetails(userDao.getUserByUsername(username));

        return user;
    }

    public void createAndSaveUser(String username, String password, String email) {

        String encryptedPass = passwordEncoder.encode(password);
        User user = new User(username, encryptedPass, email);
        user.setRoles(Roles.getDefaultRoles());

        int userId = userDao.createUser(user);

        String token = userDao.createConfirmationToken(userId);

        scheduledEmails.sendEmail("Please confirm your registration", getContentForConfirmationEmail(token), email);
    }

    public void confirmRegistration(String token) {
        Integer userId = userDao.getUserByConfirmationToken(token);

        if (userId == null) {
            throw new InvalidInputException("Invalid Token for user Confirmation");
        }

        userDao.enableUserAndDeleteConfirmationToken(userId);
    }

    private String getContentForConfirmationEmail(String token) {
        return "Click this link to confirm registration at " + baseUrl + "\n\n" + getConfirmationUrlFromToken(token);
    }

    private String getConfirmationUrlFromToken(String token) {
        return baseUrl + "/register/confirmation?token=" + token;
    }
}
