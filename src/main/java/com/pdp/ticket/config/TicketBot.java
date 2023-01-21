package com.pdp.ticket.config;

import com.pdp.ticket.model.BotState;
import com.pdp.ticket.model.Role;
import com.pdp.ticket.model.User;
import com.pdp.ticket.service.impl.*;
import com.pdp.ticket.util.StorageOperation;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TicketBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Contact contact = message.getContact();
            User user = StorageOperation.getUserWithChatId(message.getChat().getId().toString());
            if (messageText != null && messageText.equals("/start")) {
                if (user.getPhoneNumber() == null) {
                    sendMessage(BotServiceImpl.getInstance().askContactNumber(message));
                } else {
                    sendMessage(BotServiceImpl.getInstance().backToMenu(user, message));
                }
            } else if (contact != null && user.getBotState().equals(BotState.SHARE_CONTACT)) {
                List<SendMessage> login = BotServiceImpl.getInstance().login(message);
                sendMessage(login.get(1));
                sendMessage(login.get(0));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.BUS_ADD_NAME)) {
                sendMessage(BusServiceImpl.getInstance().askNumber(message));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.BUS_ADD_NUMBER)) {
                sendMessage(BusServiceImpl.getInstance().askNumberOfSeats(message));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.BUS_ADD_SEAT)) {
                sendMessage(BusServiceImpl.getInstance().createNewBuss(message));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.DESTINATION_ADD_NAME)) {
                sendMessage(DestinationServiceImpl.getDestinationServiceImpl().createdNewDestination(message));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.TRAVEL_ADD_DEPARTURE_TIME)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addArrivalTimeTravel(message));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.TRAVEL_ADD_ARRIVAL_TIME)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addBusTravel(message));
            } else if (user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.TRAVEL_ADD_PRICE_FOR_PER_SEAT)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addCreatedTimeTravel(message));
            }


        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = StorageOperation.getUserWithChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            String data = update.getCallbackQuery().getData();
            if (data.equals("bus_operation") &&
                    (user.getBotState().equals(BotState.ADMIN_MENU) || user.getBotState().equals(BotState.BUS_SHOW_BUS)) &&
                    user.getRole().equals(Role.ADMIN)) {
                sendMessage(BusServiceImpl.getInstance().openBus(update.getCallbackQuery()));
            } else if (data.equals("back_to_admin_menu")) {
                sendMessage(AdminServiceImpl.getInstance().backToMenu(update.getCallbackQuery()));
            } else if (data.equals("create_bus") && user.getBotState().equals(BotState.BUS_MENU) && user.getRole().equals(Role.ADMIN)) {
                sendMessage(BusServiceImpl.getInstance().addBus(update.getCallbackQuery()));
            } else if (data.equals("show_buses") && user.getBotState().equals(BotState.BUS_MENU) && user.getRole().equals(Role.ADMIN)) {
                sendMessage(BusServiceImpl.getInstance().showBuses(update.getCallbackQuery()));
            } else if (data.equals("destination_operation")
                    && (user.getBotState().equals(BotState.ADMIN_MENU) || user.getBotState().equals(BotState.DESTINATION_SHOW_DESTINATION))
                    && user.getRole().equals(Role.ADMIN)) {
                sendMessage(DestinationServiceImpl.getDestinationServiceImpl().openDestination(callbackQuery));
            } else if (data.equals("create_destination") && user.getBotState().equals(BotState.DESTINATION_MENU) && user.getRole().equals(Role.ADMIN)) {
                sendMessage(DestinationServiceImpl.getDestinationServiceImpl().addDestination(callbackQuery));
            } else if (data.equals("show_destination") && user.getBotState().equals(BotState.DESTINATION_MENU) && user.getRole().equals(Role.ADMIN)) {
                sendMessage(DestinationServiceImpl.getDestinationServiceImpl().showDestination(callbackQuery));
            } else if (data.equals("travel_operation") && user.getBotState().equals(BotState.ADMIN_MENU) && user.getRole().equals(Role.ADMIN)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().openTravel(callbackQuery));
            } else if (data.equals("create_travel") && user.getBotState().equals(BotState.TRAVEL_MENU) && user.getRole().equals(Role.ADMIN)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addFromTravel(callbackQuery));
            } else if (data.startsWith("from_to") && user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.TRAVEL_ADD_FROM)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addToTravel(callbackQuery));
            } else if (data.startsWith("from_to") && user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.TRAVEL_ADD_TO)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addDepartureTimeTravel(callbackQuery));
            } else if (data.startsWith("travel_bus_") && user.getRole().equals(Role.ADMIN) && user.getBotState().equals(BotState.TRAVEL_ADD_BUS)) {
                sendMessage(TravelOperationImpl.getTravelOperationImpl().addPriceForPerSeat(callbackQuery));
            } else if (data.startsWith("buy_ticket")&& user.getRole().equals(Role.USER)&&user.getBotState().equals(BotState.USER_OPEN_MENU)) {
                sendMessage(UserServiceImpl.getInstance().openBuyTicket(callbackQuery));
            } else if (data.startsWith("my_history")) {

            }
        }

    }

    public void sendMessage(Object object) {
        try {
            if (object instanceof SendMessage) {
                execute((SendMessage) object);
            }
            if (object instanceof SendPhoto) {
                execute((SendPhoto) object);
            }
            if (object instanceof EditMessageReplyMarkup) {
                execute((EditMessageReplyMarkup) object);
            }
            if (object instanceof EditMessageText) {
                execute((EditMessageText) object);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return "token";
    }

    @Override
    public String getBotUsername() {
        return "b26NewBot";
    }
}
