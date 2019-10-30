package database;


import java.util.List;

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
    public static String createCommaDelimatedStringForDatabase(List<String> strings) {
        if (strings != null && !strings.isEmpty()) {
            return String.join(",", strings);
        }
        else {
            return null;
        }
    }
}
