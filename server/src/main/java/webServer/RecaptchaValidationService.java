package webServer;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class RecaptchaValidationService {

    @Value("${recaptcha.server.key}")
    private String serverKey;

    private static final String recaptchaUrl = "https://www.google.com/recaptcha/api/siteverify";

    public Boolean checkRecaptchaString(String stringToValidate) {

        HttpURLConnection http = null;
        try{

            String input = "secret=" + serverKey + "&response=" + stringToValidate;

            URL url =
                new URL(recaptchaUrl);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);
            http.connect();
            OutputStream outputStream = http.getOutputStream();
            outputStream.write(input.getBytes(), 0, input.getBytes().length);
            outputStream.flush();
            outputStream.close();


            InputStream inputStream = http.getInputStream();
            JSONTokener jsonTokener = new JSONTokener(inputStream);
            JSONObject returnJsonObject = new JSONObject(jsonTokener);
            return (Boolean) returnJsonObject.get("success");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }
}
