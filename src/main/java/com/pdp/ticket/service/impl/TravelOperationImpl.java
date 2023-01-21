package com.pdp.ticket.service.impl;

import com.pdp.ticket.model.*;
import com.pdp.ticket.dto.EditingTravel;
import com.pdp.ticket.service.TravelOperation;
import com.pdp.ticket.util.KeybordHelper;
import com.pdp.ticket.util.StorageOperation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class TravelOperationImpl implements TravelOperation {
    @Override
    public EditMessageText openTravel(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText("Travel operation menu");
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(getTravelOperationMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
        return editMessageText;
    }

    private InlineKeyboardMarkup getTravelOperationMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = KeybordHelper.createInlineKeyboard(2, 2);
        inlineKeyboard.get(0).get(0).setText("Show Travel");
        inlineKeyboard.get(0).get(0).setCallbackData("show_travel");

        inlineKeyboard.get(0).get(1).setText("Create travel");
        inlineKeyboard.get(0).get(1).setCallbackData("create_travel");

        inlineKeyboard.get(1).get(0).setText("Edit Travel");
        inlineKeyboard.get(1).get(0).setCallbackData("edit_travel");

        inlineKeyboard.get(1).get(1).setText("Delete Travel");
        inlineKeyboard.get(1).get(1).setCallbackData("delete_travel");

        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("back_to_admin_menu");
        inlineKeyboard.add(oneRow);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        return inlineKeyboardMarkup;
    }

    @Override
    public EditMessageText addFromTravel(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        if (getDestinationsButton(true, "") == null) {
            editMessageText.setText("Destination should be more than 2");
            List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
            oneRow.get(0).setText("Back <<");
            oneRow.get(0).setCallbackData("back_to_admin_menu");
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(List.of(oneRow));
            editMessageText.setReplyMarkup(inlineKeyboardMarkup);
            return editMessageText;
        }
        editMessageText.setReplyMarkup(getDestinationsButton(true, ""));
        editMessageText.setText("Choose 'From' ");
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_FROM);
        return editMessageText;
    }

    @Override
    public EditMessageText addToTravel(CallbackQuery callbackQuery) {
        String fromId = callbackQuery.getData().split("_")[2];
        EditingTravel editingTravel = new EditingTravel();
        editingTravel.setId(callbackQuery.getMessage().getChatId().toString());
        Travel travel = new Travel();
        travel.setId(UUID.randomUUID());
        Destination destination = StorageOperation.getDestination().stream()
                .filter(des -> des.getId().toString().equals(fromId)).findFirst().orElse(null);
        travel.setFrom(destination);
        editingTravel.setTravel(travel);
        StorageOperation.writeEditingTravel(List.of(editingTravel));

        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_TO);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMessageText.setReplyMarkup(getDestinationsButton(false, fromId));
        editMessageText.setText("Choose 'To' ");
        return editMessageText;
    }

    @Override
    public EditMessageText addDepartureTimeTravel(CallbackQuery callbackQuery) {
        String toID = callbackQuery.getData().split("_")[2];
        Destination destination = StorageOperation.getDestination().stream()
                .filter(des -> des.getId().toString().equals(toID)).findFirst().orElse(null);

        List<EditingTravel> editingTravel1 = StorageOperation.getEditingTravel();
        EditingTravel editingTravel = editingTravel1.stream()
                .filter(t -> t.getId().equals(callbackQuery.getMessage().getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel.getTravel().setTo(destination);
        StorageOperation.writeEditingTravel(editingTravel1);
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_DEPARTURE_TIME);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setText(" DepartureTime ni kiriting --> Example: 22.01.2023 16:16");
        return editMessageText;
    }

    @Override
    public SendMessage addArrivalTimeTravel(Message message) {
        SendMessage editMessageText = new SendMessage();
        editMessageText.setChatId(message.getChatId());
        String departureTime = message.getText();
        if (!departureTime.matches("[0-9]{2}[.][0-9]{2}[.][0-9]{4} [0-9]{2}:[0-9]{2}")) {
            editMessageText.setText(" DepartureTime ni noto`g`ri formatda kiritdingiz!");
            editMessageText.setReplyMarkup(getTravelOperationMenu());
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_MENU);
            return editMessageText;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(departureTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        List<EditingTravel> editingTravels = StorageOperation.getEditingTravel();
        EditingTravel editingTravel = editingTravels.stream()
                .filter(edit -> edit.getId().equals(message.getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel.getTravel().setDepartureTime(localDateTime);
        StorageOperation.writeEditingTravel(editingTravels);
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_ADD_ARRIVAL_TIME);
        editMessageText.setText(" ArrivalTime ni togri namunadagidek kiriting -->\n  Example: 22.01.2023 16:16 ");
        return editMessageText;
    }

    @Override
    public SendMessage addBusTravel(Message message) {
        SendMessage editMessageText = new SendMessage();
        editMessageText.setChatId(message.getChatId());
        String arrivalTime = message.getText();
        if (!arrivalTime.matches("[0-9]{2}[.][0-9]{2}[.][0-9]{4} [0-9]{2}:[0-9]{2}")) {
            editMessageText.setText(" ArrivalTime ni noto`g`ri kiritdingiz! ");
            editMessageText.setReplyMarkup(getTravelOperationMenu());
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_MENU);
            return editMessageText;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(arrivalTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        List<EditingTravel> editingTravels = StorageOperation.getEditingTravel();
        EditingTravel editingTravel = editingTravels.stream()
                .filter(edit -> edit.getId().equals(message.getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel.getTravel().setArrivalTime(localDateTime);
        StorageOperation.writeEditingTravel(editingTravels);

        StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_ADD_BUS);
        List<Bus> buses = StorageOperation.getBuses().stream().filter(Bus::isActive).toList();
        editMessageText.setText(" Choose Bus :)\n " + showBuses(buses));
        editMessageText.setReplyMarkup(showBusButton(buses));
        return editMessageText;
    }

    @Override
    public SendMessage addPriceForPerSeat(CallbackQuery callbackQuery) {
        String busId = callbackQuery.getData().split("_")[2];
        List<Bus> buses = StorageOperation.getBuses();
        Bus bus1 = buses.stream().filter(bus -> bus.getId().toString().equals(busId)).findFirst().orElse(new Bus());
        List<EditingTravel> editingTravel = StorageOperation.getEditingTravel();
        EditingTravel editingTravel2 = editingTravel.stream().filter(editingTravel1 -> editingTravel1.getId().equals(callbackQuery.getMessage().getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel2.getTravel().setBus(bus1);
        StorageOperation.writeEditingTravel(editingTravel);
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_PRICE_FOR_PER_SEAT);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("enter price for per seat:");
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        return sendMessage;
    }

    @Override
    public SendMessage addCreatedTimeTravel(Message message) {
        SendMessage sendMessage = new SendMessage();
        String messageText = message.getText();
        List<EditingTravel> editingTravel = StorageOperation.getEditingTravel();
        EditingTravel editingTravel2 = editingTravel.stream().filter(editingTravel1 -> editingTravel1.getId().equals(message.getChatId().toString())).findFirst().orElse(new EditingTravel());
        Travel travel = editingTravel2.getTravel();
        travel.setPriceForPerSeat(BigDecimal.valueOf(Long.parseLong(messageText)));
        travel.setStatus(TravelStatus.NEW);
        travel.setCreatedTime(LocalDateTime.now());
        StorageOperation.writeEditingTravel(new ArrayList<>());
        StorageOperation.writeTravel(travel);
        StorageOperation.updateUserState(message.getChatId().toString(),BotState.TRAVEL_MENU);
        Bus bus = travel.getBus();
        List<Ticket> tickets = new ArrayList<>();
        int numberOfSeats = bus.getNumberOfSeats();
        for (int i = 0; i < numberOfSeats; i++) {
            Ticket ticket = new Ticket();
            ticket.setId(UUID.randomUUID());
            ticket.setPrice(travel.getPriceForPerSeat());
            ticket.setTravel(travel);
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setSeatNumber(i+1);
            tickets.add(ticket);
        }
        List<Ticket> allTickets = StorageOperation.getTickets();
        allTickets.addAll(tickets);
        StorageOperation.writeTickets(allTickets);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Succesfully added");
        sendMessage.setReplyMarkup(getTravelOperationMenu());
        return sendMessage;
    }

    private InlineKeyboardMarkup getDestinationsButton(boolean isFrom, String fromId) {
        List<Destination> destination = StorageOperation.getDestination();
        if (destination.size() < 2) {
            return null;
        }
        if (!isFrom) {
            destination = destination.stream().filter(destination1 -> !destination1.getId().toString().equals(fromId)).toList();
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int remainder = destination.size() % 3;
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < destination.size() - remainder; i++) {
            if (i % 3 == 2) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(destination.get(i).getName());
                button.setCallbackData("from_to_" + destination.get(i).getId());
                row.add(button);
                buttons.add(row);
                row = new ArrayList<>();
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(destination.get(i).getName());
                button.setCallbackData("from_to_" + destination.get(i).getId());
                row.add(button);
            }
        }
        if (remainder > 0) {
            for (int i = destination.size() - remainder; i < destination.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(destination.get(i).getName());
                button.setCallbackData("from_to_" + destination.get(i).getId());
                row.add(button);
            }
            buttons.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    private static final TravelOperationImpl travelOperationImpl = new TravelOperationImpl();

    public static TravelOperationImpl getTravelOperationImpl() {
        return travelOperationImpl;
    }

    private String showBuses(List<Bus> buses) {
        StringBuilder busfilds = new StringBuilder();
        for (int i = 0; i < buses.size(); i++) {
            busfilds.append(i + 1).append(". Name: ")
                    .append(buses.get(i).getName()).
                    append("\nnumber: ")
                    .append(buses.get(i).getNumber())
                    .append("\nnumber of seats: ")
                    .append(buses.get(i).getNumberOfSeats()).append("\n=======\n");
        }
        return busfilds.toString();
    }

    private InlineKeyboardMarkup showBusButton(List<Bus> buses) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int n = buses.size() % 4;
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < buses.size() - n; i++) {
            if (i % 4 == 3) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf((i + 1)));
                button.setCallbackData("travel_bus_" + buses.get(i).getId());
                row.add(button);
                buttons.add(row);
                row = new ArrayList<>();
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf((i + 1)));
                button.setCallbackData("travel_bus_" + buses.get(i).getId());
                row.add(button);
            }
        }
        if (n > 0) {
            for (int i = buses.size() - n; i < buses.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf((i + 1)));
                button.setCallbackData("travel_bus_" + buses.get(i).getId());
                row.add(button);
            }
            buttons.add(row);

        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }
}
