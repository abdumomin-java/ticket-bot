package com.pdp.ticket.service.impl;

import com.pdp.ticket.model.BotState;
import com.pdp.ticket.service.AdminService;
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

public class AdminServiceImpl implements AdminService {

    private static final AdminService adminService = new AdminServiceImpl();

    public static AdminService getInstance() {
        return adminService;
    }

    @Override
    public SendMessage openAdminMenu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose menu");
        sendMessage.setReplyMarkup(mainMenuButtons());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    @Override
    public EditMessageText backToMenu(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setReplyMarkup(mainMenuButtons());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setText("Main menu");
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.ADMIN_MENU);
        return editMessageText;
    }

    private InlineKeyboardMarkup mainMenuButtons() {
        List<List<InlineKeyboardButton>> inlineKeyboard = KeybordHelper.createInlineKeyboard(2, 2);
        inlineKeyboard.get(0).get(0).setText("Bus operation");
        inlineKeyboard.get(0).get(0).setCallbackData("bus_operation");

        inlineKeyboard.get(0).get(1).setText("Travel operation");
        inlineKeyboard.get(0).get(1).setCallbackData("travel_operation");

        inlineKeyboard.get(1).get(0).setText("Destination operation");
        inlineKeyboard.get(1).get(0).setCallbackData("destination_operation");

        inlineKeyboard.get(1).get(1).setText("About us");
        inlineKeyboard.get(1).get(1).setCallbackData("about us");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        return inlineKeyboardMarkup;
    }
}
