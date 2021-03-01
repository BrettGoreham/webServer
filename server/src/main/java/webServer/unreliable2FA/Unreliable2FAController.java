package webServer.unreliable2FA;

import database.APIKeyDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;
import unrelaiable2FA.api.AuthenticateApiController;
import unrelaiable2FA.model.AuthenticateRequest;
import unrelaiable2FA.model.GenerationRequest;
import webServer.exceptions.InvalidInputException;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Controller
public class Unreliable2FAController extends AuthenticateApiController {

    private final SecureRandom randomGenerator;
    private APIKeyDao apiKeyDao;

    public Unreliable2FAController(NativeWebRequest request, APIKeyDao apiKeyDao) {
        super(request);
        randomGenerator = new SecureRandom();
        this.apiKeyDao = apiKeyDao;
    }

    @Override
    public ResponseEntity<Boolean> authenticatePost(@Valid AuthenticateRequest authenticateRequest) {

        boolean isAuthenticated = apiKeyDao.authenticateToken((long) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), authenticateRequest.getExternalUserId(), authenticateRequest.getToken());

        return new ResponseEntity<>(isAuthenticated, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> authenticateGet(@Valid GenerationRequest generationRequest) {

        String token = generateToken(
                generationRequest.getGenerationType(),
                generationRequest.getGenerationLength()
        );

        apiKeyDao.insertToken(
                (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                token,
                LocalDateTime.now().plusMinutes(generationRequest.getTokenMinutesToLive()),
                generationRequest.getExternalUserId());

        if (generationRequest.getReturnToken()) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("", HttpStatus.OK);
        }

    }


    private String generateToken(GenerationRequest.GenerationTypeEnum mode, int length) {
        switch (mode) {
            case STRING:
                return generateStringToken(length);
            case INT:
                return generateIntToken(length);
            default:
                throw new InvalidInputException("expected \"string\" or \"int\" as generation type");
        }
    }

    private String generateStringToken(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) (65 + randomGenerator.nextInt(26)));//65 being A A+25 = Z
        }
        return result.toString();
    }

    private String generateIntToken(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(randomGenerator.nextInt(10));
        }
        return result.toString();
    }
}
