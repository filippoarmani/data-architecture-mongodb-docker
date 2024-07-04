package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MongoDBShardedConnection {

    public static List<Document> getData() {
        List<Document> airportDocuments = new ArrayList<>();

        try (FileReader reader = new FileReader("Airports Modeling Export.json")) {
            // Crea un JSONTokener dal FileReader
            JSONTokener tokener = new JSONTokener(reader);

            // Crea un JSONArray dal tokener
            JSONArray jsonArray = new JSONArray(tokener);

            // Itera attraverso gli elementi dell'array JSON
            for (int i = 0; i < jsonArray.length(); i++) {
                // Ottiene l'oggetto JSON corrente
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Estrae i campi desiderati per il documento dell'aeroporto
                String geoPoint = jsonObject.optString("Geo_Point", "");
                String name = jsonObject.optString("Name", "");
                String nameEn = jsonObject.optString("Name_(en)", "");
                String nameFr = jsonObject.optString("Name_(fr)", "");
                String iataCode = jsonObject.optString("IATA_code", "");
                String icaoCode = jsonObject.optString("ICAO_code", "");
                String operator = jsonObject.optString("Operator", "");
                String country = jsonObject.optString("Country", "");
                String countryCode = jsonObject.optString("Country_code", "");
                int size = jsonObject.optInt("Size", 0);

                // Estrae l'array di voli per l'aeroporto
                JSONArray flightsArray = jsonObject.getJSONArray("Flights");
                List<Flight> flights = new ArrayList<>();

                // Itera attraverso gli elementi dell'array dei voli
                for (int j = 0; j < flightsArray.length(); j++) {
                    JSONObject flightObject = flightsArray.getJSONObject(j);

                    // Estrae i campi desiderati per il volo
                    String flightId = flightObject.optString("ID", "");
                    int numberOfSeats = flightObject.optInt("Number_of_Seats", 0);
                    String day = flightObject.optString("Day", "");
                    String hour = flightObject.optString("Hour", "");
                    String flightOperator = flightObject.optString("Operator", "");
                    String duration = flightObject.optString("Duration", "");
                    int pricePerPerson = flightObject.optInt("Price_per_Person", 0);

                    // Estrae l'array dei posti per il volo
                    JSONArray seatsArray = flightObject.getJSONArray("Seats");
                    List<Seat> seats = new ArrayList<>();

                    // Itera attraverso gli elementi dell'array dei posti
                    for (int k = 0; k < seatsArray.length(); k++) {
                        JSONObject seatObject = seatsArray.getJSONObject(k);

                        // Estrae i campi desiderati per il posto
                        String status = seatObject.optString("Status", "");
                        String seatId = seatObject.optString("ID", "");
                        String nameOnTicket = seatObject.optString("Name", "");
                        String surnameOnTicket = seatObject.optString("Surname", "");
                        String documentInfo = seatObject.optString("Document_Info", "");
                        String dateOfBirth = seatObject.optString("Date_of_Birth", "");
                        int balance = seatObject.optInt("Balance", 0);

                        // Crea un oggetto Seat e aggiungilo alla lista dei posti
                        Seat seat = new Seat(status, seatId, nameOnTicket, surnameOnTicket, documentInfo, dateOfBirth, balance);
                        seats.add(seat);
                    }

                    // Crea un oggetto Flight e aggiungilo alla lista dei voli
                    Flight flight = new Flight(flightId, numberOfSeats, day, hour, flightOperator, duration, pricePerPerson, seats);
                    flights.add(flight);
                }

                // Crea un oggetto Document per l'aeroporto e aggiungilo alla lista dei documenti
                Airport document = new Airport(geoPoint, name, nameEn, nameFr, iataCode, icaoCode, operator, country, countryCode, size, flights);
                airportDocuments.add(document.toDocument());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return airportDocuments;
    }


    public static void main(String[] args) {
        // Connessione al router mongos
        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("myDatabase");
            System.out.println("Connesso al database: " + database.getName());

            MongoCollection<Document> collection = database.getCollection("myCollection");
            List<Document> airports = getData();
            collection.insertMany(airports);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
