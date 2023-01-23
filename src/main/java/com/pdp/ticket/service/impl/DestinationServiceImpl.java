package com.pdp.ticket.service.impl;

import com.pdp.ticket.enam.BotState;
import com.pdp.ticket.model.Destination;
import com.pdp.ticket.service.DestinationService;
import com.pdp.ticket.util.KeybordHelper;
import com.pdp.ticket.util.StorageOperation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DestinationServiceImpl implements DestinationService {

    @Override
    public EditMessageText openDestination(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText("Destination operation menu");
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(getDestinationOperationMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.DESTINATION_MENU);
        return editMessageText;
    }

    private InlineKeyboardMarkup getDestinationOperationMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = KeybordHelper.createInlineKeyboard(2, 2);
        inlineKeyboard.get(0).get(0).setText("Show destination");
        inlineKeyboard.get(0).get(0).setCallbackData("show_destination");

        inlineKeyboard.get(0).get(1).setText("Create destination");
        inlineKeyboard.get(0).get(1).setCallbackData("create_destination");

        inlineKeyboard.get(1).get(0).setText("Edit destination");
        inlineKeyboard.get(1).get(0).setCallbackData("edit_destination");

        inlineKeyboard.get(1).get(1).setText("Delete destination");
        inlineKeyboard.get(1).get(1).setCallbackData("delete_destination");

        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("back_to_admin_menu");
        inlineKeyboard.add(oneRow);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        return inlineKeyboardMarkup;
    }

    @Override
    public EditMessageText addDestination(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.DESTINATION_ADD_NAME);
        editMessageText.setText("Enter destination name: ");
        return editMessageText;
    }

    @Override
    public SendMessage createdNewDestination(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        String name = message.getText();
        if (Objects.isNull(name) || name.isBlank()) {
            sendMessage.setText(" Name bo`sh bo`lmasligi kerak, Qayta uruning! ");
            sendMessage.setReplyMarkup(getDestinationOperationMenu());
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.DESTINATION_MENU);
            return sendMessage;
        }
        Destination destination = new Destination();
        destination.setId(UUID.randomUUID());
        destination.setName(name);
        StorageOperation.writeDestination(destination);
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.DESTINATION_MENU);
        sendMessage.setReplyMarkup(getDestinationOperationMenu());
        sendMessage.setText("Successfully create Destination ");
        return sendMessage;
    }

    @Override
    public EditMessageText showDestination(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.DESTINATION_SHOW_DESTINATION);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("destination_operation");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(oneRow));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        List<Destination> destinations = StorageOperation.getDestination();
        if (destinations.size() == 0) {
            editMessageText.setText(" There is not any Destination ");
            return editMessageText;
        }
        StringBuilder text = new StringBuilder();
        for (Destination bus : destinations) {
            text.append("Name: ")
                    .append(bus.getName())
                    .append("\n=======\n");
        }
        editMessageText.setText(text.toString());
        return editMessageText;
    }

    private static final DestinationServiceImpl destinationServiceImpl = new DestinationServiceImpl();

    public static DestinationServiceImpl getDestinationServiceImpl() {
        return destinationServiceImpl;
    }
}
