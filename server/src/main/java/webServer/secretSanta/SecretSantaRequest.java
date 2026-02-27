package webServer.secretSanta;

import java.util.HashMap;
import java.util.List;

public class SecretSantaRequest {

    public Person[] people;
    public HashMap<String, List<String>> blocking;
    public String recaptchaResult;

    public Person[] getPeople() {
        return people;
    }

    public void setPeople(Person[] people) {
        this.people = people;
    }

    public HashMap<String, List<String>> getBlocking() {
        return blocking;
    }

    public void setBlocking(HashMap<String, List<String>> blocking) {
        this.blocking = blocking;
    }

    public String getRecaptchaResult() {
        return recaptchaResult;
    }

    public void setRecaptchaResult(String recaptchaResult) {
        this.recaptchaResult = recaptchaResult;
    }
}
