package com.pdp.ticket.service.impl;

import com.pdp.ticket.enam.BotState;
import com.pdp.ticket.model.Bus;
import com.pdp.ticket.dto.EditingBus;
import com.pdp.ticket.service.BusService;
import com.pdp.ticket.util.KeybordHelper;
import com.pdp.ticket.util.StorageOperation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BusServiceImpl implements BusService {
    private static final BusService busService = new BusServiceImpl();

    public static BusService getInstance() {
        return busService;
    }

    @Override
    public EditMessageText openBus(CallbackQuery callbackQuery) {

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText("Bus operation menu");
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(getBusOperationMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.BUS_MENU);

        return editMessageText;
    }

    private InlineKeyboardMarkup getBusOperationMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = KeybordHelper.createInlineKeyboard(2, 2);
        inlineKeyboard.get(0).get(0).setText("Show buses");
        inlineKeyboard.get(0).get(0).setCallbackData("show_buses");

        inlineKeyboard.get(0).get(1).setText("Create bus");
        inlineKeyboard.get(0).get(1).setCallbackData("create_bus");

        inlineKeyboard.get(1).get(0).setText("Edit bus");
        inlineKeyboard.get(1).get(0).setCallbackData("edit_bus");

        inlineKeyboard.get(1).get(1).setText("Delete bus");
        inlineKeyboard.get(1).get(1).setCallbackData("delete_bus");

        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("back_to_admin_menu");
        inlineKeyboard.add(oneRow);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        return inlineKeyboardMarkup;
    }

    @Override
    public EditMessageText addBus(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMessageText.setText("Enter bus name: ");
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.BUS_ADD_NAME);
        return editMessageText;
    }

    @Override
    public SendMessage askNumber(Message message) {
        String busName = message.getText();
        EditingBus editingBus = new EditingBus();
        editingBus.setChatId(message.getChatId().toString());
        Bus bus = new Bus();
        bus.setId(UUID.randomUUID());
        bus.setName(busName);
        editingBus.setBus(bus);
        StorageOperation.writeEditingBus(List.of(editingBus));
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.BUS_ADD_NUMBER);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Enter bus number");
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

    @Override
    public SendMessage askNumberOfSeats(Message message) {
        String number = message.getText();
        List<EditingBus> editingBus = StorageOperation.getEditingBus();
        EditingBus editingBus1 = editingBus.stream().filter(e -> e.getChatId().equals(message.getChatId().toString())).findFirst().orElse(new EditingBus());
        editingBus1.getBus().setNumber(number);
        StorageOperation.writeEditingBus(editingBus);
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.BUS_ADD_SEAT);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Enter number of seats");
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

    @Override
    public SendMessage createNewBuss(Message message) {
        String number = message.getText();
        EditingBus editingBusChatId = StorageOperation.getEditingBusChatId(message.getChatId().toString());
        Bus bus = editingBusChatId.getBus();
        bus.setNumberOfSeats(Integer.parseInt(number));
        bus.setActive(true);
        StorageOperation.writeBus(bus);
        StorageOperation.writeEditingBus(new ArrayList<>());
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.BUS_MENU);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Successfully added!");
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(getBusOperationMenu());
        return sendMessage;
    }

    @Override
    public EditMessageText showBuses(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.BUS_SHOW_BUS);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("bus_operation");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(oneRow));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        List<Bus> buses = StorageOperation.getBuses();
        if (buses.isEmpty()) {
          editMessageText.setText("There is not any bus!");
          return editMessageText;
        }
        StringBuilder text = new StringBuilder();
        for (Bus bus : buses) {
            if (bus.isActive()){
                text.append("Name: ")
                        .append(bus.getName())
                        .append("\nNumber: ")
                        .append(bus.getNumber())
                        .append("\nNumber of seats: ")
                        .append(bus.getNumberOfSeats())
                        .append("\n=======\n");
            }
        }
        editMessageText.setText(text.toString());
        return editMessageText;
    }
}
