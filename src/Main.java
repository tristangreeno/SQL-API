import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        ReadPersonFile.ReadPeopleFile();

        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    boolean isZero = true;
                    Session s = request.session();
                    Integer offset = 0;
                    HashMap map = new HashMap<String, ArrayList<Person>>();
                    String offsetStr = request.queryParams("offset");

                    if(offsetStr != null && ! offsetStr.equals("") && Integer.valueOf(offsetStr) >= 0){
                        offset = Integer.valueOf(offsetStr);
                    }

                    ArrayList<Person> offsetPersons = new ArrayList<>();

                    for(int i = 0; i < 20; i++) offsetPersons.add(ReadPersonFile.persons.get(i + offset));

                    s.attribute("offset", offset);

                    if(offset != 0){
                        isZero = false;
                    }

                    map.put("isZero", isZero);

                    map.put("offset", offset);
                    map.put("people", offsetPersons);

                    return new ModelAndView(map, "homepage.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                (request, response) -> {
                    HashMap m = new HashMap();
                    Integer id = Integer.valueOf(request.queryParams("id"));

                    // Subtract one to get correct index
                    Person p = ReadPersonFile.persons.get(id - 1);

                    m.put("person", p);
                    return new ModelAndView(m, "person.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/next",
                (request, response) -> {
                    Session s = request.session();
                    Integer offset = s.attribute("offset");
                    response.redirect("/?offset=" + (offset + 20));
                    return "";
                }
        );

        Spark.post(
                "/previous",
                (request, response) -> {
                    Session s = request.session();
                    Integer offset = s.attribute("offset");

                    if(offset >= 20) response.redirect("/?offset=" + (offset - 20));
                    else response.redirect("/?offset=" + offset);

                    return "";
                }
        );



    }
}
