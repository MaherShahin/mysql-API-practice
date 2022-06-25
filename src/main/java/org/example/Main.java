package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        populateDatabase();

    }

    public static Person generateRandomUserAPI() throws IOException {
        Person person = new Person();

        HttpURLConnection connection = (HttpURLConnection) new URL("https://randomuser.me/api/").openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode==200){
            String response = "";
            Scanner sc = new Scanner(connection.getInputStream());
            while (sc.hasNextLine()){
                response += sc.nextLine();
                response += "\n";
            }

            JSONObject resultJson = new JSONObject(response).getJSONArray("results").getJSONObject(0);


            String firstName = resultJson.getJSONObject("name").getString("first");
            String lastName = resultJson.getJSONObject("name").getString("last");
            String address = resultJson.getJSONObject("location").getJSONObject("street").getString("name")
                    + " "
                    +resultJson.getJSONObject("location").getJSONObject("street").get("number").toString();
            String city = resultJson.getJSONObject("location").getString("city");
            String email = resultJson.getString("email");
            String phone = resultJson.getString("phone");

            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setAddress(address);
            person.setCity(city);
            person.setEmail(email);
            person.setPhone(phone);

        }

        return person;

    }

    public static void populateDatabase() throws SQLException, IOException {

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test1","root","IamNotThatStupid");

        String  sql ="INSERT INTO persons (LastName, FirstName, Address, City, Email, Phone) VALUES (?,?,?,?,?,?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ArrayList<Person> personArrayList = new ArrayList<>();

        for (int i = 0; i<20; i++){
            personArrayList.add(generateRandomUserAPI());
        }

        for (Person p: personArrayList) {
            ps.setObject(1,p.getLastName());
            ps.setObject(2,p.getFirstName());
            ps.setObject(3,p.getAddress());
            ps.setObject(4,p.getCity());
            ps.setObject(5,p.getEmail());
            ps.setObject(6,p.getPhone());
            ps.executeUpdate();
        }

        connection.close();

    }
}