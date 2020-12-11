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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return new SecurityUserDetails(userDao.getUserByUsername(username));
    }

    public void createAndSaveUser(String username, String password, String email) {

        String encryptedPass = passwordEncoder.encode(password);
        User user = new User(username, encryptedPass, email);
        user.setRoles(Roles.getDefaultRoles());

        int userId = userDao.createUser(user);

        String token = userDao.createConfirmationToken(userId);

        scheduledEmails.sendConfirmationEmail(token, email);
    }

    public void confirmRegistration(String token) {
        Integer userId = userDao.getUserByConfirmationToken(token);

        if (userId == null) {
            throw new InvalidInputException("Invalid Token for user Confirmation");
        }

        userDao.enableUserAndDeleteConfirmationToken(userId);
    }

    /*private String getContentForConfirmationEmail(String token) {
        return "<div>" +
                    "<h5>Click this link to confirm registration at " + baseUrl + "</h5>" +
                    "<br/><br/>"  +
                    "<a href=\""+ getConfirmationUrlFromToken(token) +  "\">Click here to confirm account</a>" +
                "</div>" ;
    }

    private String getConfirmationUrlFromToken(String token) {
        return baseUrl + "/register/confirmation?token=" + token;
    }*/
}
