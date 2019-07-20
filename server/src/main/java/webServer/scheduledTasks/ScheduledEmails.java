package webServer.scheduledTasks;

import database.MealDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
public class ScheduledEmails {

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private MealDao mealDao;

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

            sendEmailToBrett("Nightly Suggestion Check " + date, stringBuilder.toString());

        }

    }

    private void sendEmailToBrett(String subject, String content) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gorehamwebserver@gmail.com");
        message.setTo("brettgoreham@gmail.com");
        message.setSubject(subject);
        message.setText(content);
        try {
            emailSender.send(message);
            System.out.println("Sent email.");
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
}
