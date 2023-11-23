package database;


import java.util.List;
import java.util.stream.Collectors;

public class DatabaseUtil {

    private DatabaseUtil(){}

    //inverse of createCommaDelimatedStringForDatabase
    public static List<String> splitCommaDelimatedStringFromDatabase(String commaSeperatedList){
        if (commaSeperatedList != null) {
            return List.of(commaSeperatedList.split(","));
        }
        else {
            return null;
        }
    }

    //inverse of splitCommaDelimatedStringFromDatabase
    public static <T> String createCommaDelimatedStringForDatabase(List<T> strings) {
        if (strings != null && !strings.isEmpty()) {
            return strings
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        }
        else {
            return null;
        }
    }

    public static boolean createBooleanFromDatabase(int booleanEquivalentVal){
        return booleanEquivalentVal != 0; //0 represented as false in database anything else = true
    }

    public static int createBooleanEquivalentIntForDatabase(boolean bool) {
        if (bool) {
            return 1;
        } else {
            return 0;
        }
    }
}
