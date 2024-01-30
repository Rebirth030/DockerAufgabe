package com.example.abgabedocker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class DockerControler {

    /**
     * Main Method
     * Ich wollte mir hierbei mal daran versuchen, dass ganze ohne Apis und mir so ein bisschen mehr challange beim code zu geben, nur um mal auszuprobieren, ob ich es kann)
     * Ich hoffe das stört euch nicht
     * @param args
     */
    public static void main(String[] args) {
        try {
            URL url = new URL("http://host.docker.internal:8080/v1/result");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            byte[] input = makeJsonFormat(calculateTime(getData())).getBytes("utf-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            connection.getOutputStream().write(input, 0, input.length);
            System.out.println("Response Code : " + connection.getResponseCode());
            System.out.println("Response Message : " + connection.getResponseMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get Data from the API
     * Use delimiter to extract the data out of the event[] array
     * split customer objects at },{
     * remove first { and } at the beginning and end of the string (left over at the beginning and end of the whole text)
     * split the string at , to get the single attributes
     * split the attributes at : to get the value -> create new customer object and add it to the list
     *
     * @return ArrayList<Customer>
     */
    public static ArrayList<Customer> getData() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            URL url = new URL("http://host.docker.internal:8080/v1/dataset");
            Scanner scanner = new Scanner(url.openStream());
            scanner.useDelimiter("[\\[ \\]]");
            scanner.next();
            String s = scanner.next();
            scanner.next();
            String[] s1 = s.split("},\\{");
            for (String s2 : s1) {
                s2 = s2.replaceAll("}", "").replaceAll("\\{", "");
                String[] t = s2.split(",");
                customers.add(new Customer(t[0].split(":")[1], t[1].split(":")[1], Long.parseLong(t[2].split(":")[1]), t[3].split(":")[1]));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    /**
     * Calculate the time between start and stop of a customer
     * and create times2, to add up the times of the same customer
     * @param customers
     * @return ArrayList<String [ ]>
     */
    public static ArrayList<result> calculateTime(ArrayList<Customer> customers) {
        ArrayList<result> times = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getEventType().contains("start")) {
                for (Customer c2 : customers) {
                    if (c.getCustomerId().contains(c2.getCustomerId()) && c.getWorkloadId().contains(c2.getWorkloadId()) && c2.getEventType().contains("stop")) {
                        times.add(new result(c.getCustomerId(), c2.getTimestamp() - c.getTimestamp()));
                    }
                }
            }
        }

        ArrayList<result> times2 = new ArrayList<>();
        for (result r : times) {
            Long l = r.getConsumption();
            boolean alreadyInTimes2 = false;
            for (result r3 : times2) { //wenn in times2 schon drinnen -> true
                if (r3.getCustomerId().contains(r.getCustomerId())) {
                    alreadyInTimes2 = true;
                }
            }
            if (!alreadyInTimes2) {
                for (result r2 : times) {
                    if (r.getCustomerId().contains(r2.getCustomerId()) && !r.equals(r2)) {
                        l += r2.getConsumption();
                    }
                }
                times2.add(new result(r.getCustomerId(), l));
            }
        }
        return times2;
    }

    /**
     * Create the Json Format for the output
     * @param results
     */
    public static String makeJsonFormat(ArrayList<result> results){
        JSONArray jsonArray = new JSONArray();
        for(result r : results){
            JSONObject jsonObject = new JSONObject();
            System.out.println(r.getCustomerId());
            jsonObject.put("customerId", r.getCustomerId());
            jsonObject.put("consumption", r.getConsumption());
            jsonArray.put(jsonObject);
        }
        JSONObject finalObject = new JSONObject();
        finalObject.put("result", jsonArray);
        String jsonString = finalObject.toString();
        jsonString = jsonString.replace("\\\"", ""); // replace \" with nothing
        System.out.println(jsonString);
        return finalObject.toString();
    }
}