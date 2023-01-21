package com.pdp.ticket.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pdp.ticket.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StorageOperation {

    private static final String USER = "src/main/resources/json/users.json";
    private static final String ORDER = "src/main/resources/json/order.json";
    private static final String BUS = "src/main/resources/json/bus.json";
    private static final String DESTINATION = "src/main/resources/json/destination.json";
    private static final String TRAVEL = "src/main/resources/json/travel.json";
    private static final String EDITING_TRAVEL = "D:\\Java codes\\model3\\download for zips\\ticket_bot_19_01\\ticket-bot\\src\\main\\resources\\json\\editingTravel.json";
//    private static final String DESTINATION = "src/main/resources/json/destination.json";

    private static final String EDITING_BUS = "src/main/resources/json/editingBus.json";

    public static List<Travel> getTravels() {
        Type type = new TypeToken<List<Travel>>() {
        }.getType();
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(TRAVEL))) {
            List<Travel> travels = gson.fromJson(reader, type);
            return travels == null ? new ArrayList<>() : travels;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void writeTravels(List<Travel> travels) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(travels);
        try {
            Files.write(Path.of(TRAVEL), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTravel(Travel bus) {
        List<Travel> travels = getTravels();
        travels.add(bus);
        writeTravels(travels);
    }

    public static List<User> getUsers() {
        Type type = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(USER))) {
            List<User> users = gson.fromJson(reader, type);
            return users == null ? new ArrayList<>() : users;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Destination> getDestination() {
        Type type = new TypeToken<List<Destination>>() {
        }.getType();
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(DESTINATION))) {
            List<Destination> destinations = gson.fromJson(reader, type);
            return destinations == null ? new ArrayList<>() : destinations;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void writeUsers(List<User> users) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(users);
        try {
            Files.write(Path.of(USER), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getUserWithChatId(String chatId) {
        return getUsers().stream().filter(user -> chatId.equals(user.getChatId())).findFirst().orElse(new User());
    }


    public static void updateUserState(String chatId, BotState botState) {
        List<User> users = getUsers();
        User user = users.stream()
                .filter(u -> u.getChatId().equals(chatId))
                .findFirst()
                .orElse(new User());
        user.setBotState(botState);
        writeUsers(users);
    }

    public static List<EditingTravel> getEditingTravel() {
        Type type = new TypeToken<List<EditingTravel>>() {
        }.getType();
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(EDITING_TRAVEL))) {
            List<EditingTravel> editingBuses = gson.fromJson(reader, type);
            return editingBuses == null ? new ArrayList<>() : editingBuses;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

//    public static EditingTravel getEditingTravelChatId(String ) {
//        return getEditingTravel().stream().filter(e -> chatId.equals(e.getId())).findFirst().orElse(new EditingTravel());
//    }

    public static void writeEditingTravel(List<EditingTravel> editingBuses) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(editingBuses);
        try {
            Files.write(Path.of(EDITING_TRAVEL), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void writeEditingTravel(List<EditingTravel> editingBuses) {
//        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
//        try (PrintWriter writer = new PrintWriter(new File(EDITING_TRAVEL))) {
//            writer.write(gson.toJson(editingBuses));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public static List<EditingBus> getEditingBus() {
        Type type = new TypeToken<List<EditingBus>>() {
        }.getType();
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(EDITING_BUS))) {
            List<EditingBus> editingBuses = gson.fromJson(reader, type);
            return editingBuses == null ? new ArrayList<>() : editingBuses;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static EditingBus getEditingBusChatId(String chatId) {
        return getEditingBus().stream().filter(e -> chatId.equals(e.getChatId())).findFirst().orElse(new EditingBus());
    }

    public static void writeEditingBus(List<EditingBus> editingBuses) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(editingBuses);
        try {
            Files.write(Path.of(EDITING_BUS), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeDestinations(List<Destination> destinations) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(destinations);
        try {
            Files.write(Path.of(DESTINATION), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeDestination(Destination bus) {
        List<Destination> destinations = getDestination();
        destinations.add(bus);
        writeDestinations(destinations);
    }

    public static List<Bus> getBuses() {
        Type type = new TypeToken<List<Bus>>() {
        }.getType();
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(BUS))) {
            List<Bus> buses = gson.fromJson(reader, type);
            return buses == null ? new ArrayList<>() : buses;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void writeBuses(List<Bus> buses) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(buses);
        try {
            Files.write(Path.of(BUS), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBus(Bus bus) {
        List<Bus> buses = getBuses();
        buses.add(bus);
        writeBuses(buses);
    }
}
