import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by tristangreeno on 4/26/16.
 */
public class Testing {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        return conn;
    }

    @Test
    public void createTables() throws SQLException {
        Connection conn = startConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS people");
        stmt.execute("CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR," +
                " country VARCHAR, ip VARCHAR)");

        assert stmt.execute("IF EXISTS people ");
    }

    @Test
    public void insertPerson() throws SQLException{
        Connection conn = startConnection();
        String firstName = "Jim";
        String lastName = "Peterson";
        String email = "email@email.com";
        String country = "Narnia";
        String ip = "1.1.1.1";

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)");

        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);

        stmt.execute();
    }

    @Test
    public void selectPerson() throws SQLException {
        Connection conn = startConnection();

        Integer id = 1;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet set = stmt.executeQuery();
        //NOTE: Forgetting set.next() throws a "No data found" error because data hasn't been pulled
        set.next();

        Person person = new Person(1, set.getString("first_name"), set.getString("last_name"), set.getString("email"),
                set.getString("country"), set.getString("ip"));

        Assert.assertNotNull(person);
    }

    @Test
    public void populateDatabase() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();
        Sql.createTables(conn);
        ArrayList<Person> persons = new ArrayList<>();

        File f = new File("src/people.csv");
        Scanner s = new Scanner(f);
        s.nextLine();

        while(s.hasNext()){
            String line = s.nextLine();
            String columns[] = line.split(",");
            Person per = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3],
                    columns[4], columns[5]);
            persons.add(per);
        }

        for (Person p : persons) {
            Sql.insertPerson(conn, p.firstName, p.lastName, p.email, p.country, p.ipAddress);
        }

        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery("SELECT * FROM people WHERE id = 332");
        set.next();
        Assert.assertNotNull(set.getString(2));
    }

    @Test
    public void selectPeople() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();
        Sql.createTables(conn);
        ReadPersonFile.populateDatabase(conn);
        ArrayList<Person> persons = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people LIMIT 20 OFFSET ?");
        //noinspection JpaQueryApiInspection
        stmt.setInt(1, 40);
        ResultSet set = stmt.executeQuery();

        while(set.next()){
            persons.add(new Person(set.getInt("id"), set.getString("first_name"), set.getString("last_name"),
                    set.getString("email"), set.getString("country"), set.getString("ip")));
        }

        for (Person p : persons) {
            System.out.println(p.id);
        }
        Assert.assertEquals(persons.size(), 20);
    }



}
