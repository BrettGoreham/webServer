package webServer.scheduledTasks;

import database.MealDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
public class ScheduledEmails {

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    public TemplateEngine templateEngine;

    @Autowired
    private MealDao mealDao;

    @Value("${spring.mail.username}")
    private String fromEmailAddr;

    @Value("${to.email.address}")
    private String toEmailAddr;

    @Value("${baseUrl}")
    private String baseUrl;

    //seconds minutes hours daysofmonth monthsofyear daysofweek"
    @Scheduled(cron = "0 0 20 * * MON-FRI")
    public void nightlyEmailForSuggestedTasks() {
        List<String> categoriesWaiting = mealDao.getSuggestedMealCategoriesExist();
        List<String> optionsWaiting = mealDao.getSuggestedMealOptionsExist();

        if (categoriesWaiting.size() > 0 || optionsWaiting.size() > 0) {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("This is the nightly suggestion status for: " + date);
            stringBuilder.append("\n");
            stringBuilder.append("\n");

            if (categoriesWaiting.size() > 0) {
                stringBuilder.append("There are " + categoriesWaiting.size() + " meal categories awaiting approval");
                stringBuilder.append("\n");
                stringBuilder.append("They are: ");
                for (String s : categoriesWaiting) {
                    stringBuilder.append("\n");
                    stringBuilder.append("\t- " + s);
                }
                stringBuilder.append("\n");
                stringBuilder.append("\n");
            }


            if (optionsWaiting.size() > 0) {
                stringBuilder.append("There are " + optionsWaiting.size() + " meal options awaiting approval");
                stringBuilder.append("\n");
                stringBuilder.append("They are: ");
                for (String s : optionsWaiting) {
                    stringBuilder.append("\n");
                    stringBuilder.append("\t- " + s);
                }
                stringBuilder.append("\n");
                stringBuilder.append("\n");

            }

            sendEmailToToEmailAddr("Nightly Suggestion Check " + date, stringBuilder.toString());

        }

    }

    public void sendEmail(String subject, String content, String emailAddress) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmailAddr);
        message.setTo(emailAddress);
        message.setSubject(subject);
        message.setText(content);
        try {
            emailSender.send(message);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void sendEmailToToEmailAddr(String subject, String content) {

        sendEmail(subject, content, toEmailAddr);
    }

    public void sendConfirmationEmail(String token, String email) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmailAddr);
            helper.setTo(email);
            helper.setSubject("Please Confirm your Registration");
            helper.setText(generateMailContent(token),true);

            emailSender.send(message);

        } catch (MessagingException e) {
            System.out.println(e);
        }
    }


    public String generateMailContent(String token) {
        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("baseUrl", baseUrl);

        return templateEngine.process("userManagement/registrationEmail", context);
    }
}
