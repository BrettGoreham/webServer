package webServer.userManagement;



import database.APIKeyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

    @Autowired
    APIKeyDao apiKeyDao;

    @PostMapping("generateApiKey")
    public String generateApiKey(Principal principal) {

        SecurityUserDetails userDetails = validatePrinciple(principal);

        return apiKeyDao.generateAndSaveUniqueApiKey(userDetails.getId()).toString();
    }

    @DeleteMapping(value = "deleteApiKey", consumes = "application/json")
    public void deleteApiKey(Principal principal, @RequestBody UUID apiKey) {
        SecurityUserDetails userDetails = validatePrinciple(principal);

        apiKeyDao.deleteApiKeyForUser(apiKey, userDetails.getId());
    }

    private SecurityUserDetails validatePrinciple(Object principal) {
        if (principal== null) {

            throw new  IllegalArgumentException("Principal can not be null!");
        }
        else {
            if (principal instanceof SecurityUserDetails ) {
                return (SecurityUserDetails) principal;
            }
            else {
                return (SecurityUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            }
        }
    }

}
