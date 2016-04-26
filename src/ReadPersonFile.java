import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads the people.csv file into usable data as a JSON.
 */
public class ReadPersonFile {
    public static ArrayList<Person> persons = new ArrayList<>();
    static ArrayList<Person> populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        File f = new File("src/people.csv");
        Scanner scanner = new Scanner(f);
        scanner.nextLine();

        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3],
                    columns[4], columns[5]);
            persons.add(p);
        }

        for (Person p : persons) {
            Sql.insertPerson(conn, p.firstName, p.lastName, p.email, p.country, p.ipAddress);
        }

        return persons;
    }
}
