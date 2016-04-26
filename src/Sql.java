import org.junit.Assert;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by tristangreeno on 4/26/16.
 */
public class Sql {
    public static Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        return conn;
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS people");
        stmt.execute("CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR," +
                " country VARCHAR, ip VARCHAR)");
    }

    public static void insertPerson(Connection conn, String firstName, String lastName, String email,
                                    String country, String ip) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)");

        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);

        stmt.execute();
    }

    public static Person selectPerson(Connection conn, Integer id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet set = stmt.executeQuery();
        //NOTE: Forgetting set.next() throws a "No data found" error because data hasn't been pulled
        set.next();

        return new Person(1, set.getString("first_name"), set.getString("last_name"), set.getString("email"),
                set.getString("country"), set.getString("ip"));
    }

    public static ArrayList<Person> selectPeople(Connection conn, Integer offset) throws SQLException, FileNotFoundException {
        Sql.createTables(conn);
        ReadPersonFile.populateDatabase(conn);
        ArrayList<Person> persons = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people LIMIT 20 OFFSET ?");
        //noinspection JpaQueryApiInspection
        stmt.setInt(1, offset);
        ResultSet set = stmt.executeQuery();

        while(set.next()){
            persons.add(new Person(set.getInt("id"), set.getString("first_name"), set.getString("last_name"),
                    set.getString("email"), set.getString("country"), set.getString("ip")));
        }

        return persons;
    }



}
