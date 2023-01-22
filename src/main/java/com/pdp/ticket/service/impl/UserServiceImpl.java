package com.pdp.ticket.service.impl;

import com.pdp.ticket.model.BotState;
import com.pdp.ticket.service.UserService;
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

public class UserServiceImpl implements UserService {

    private static final UserService userService = new UserServiceImpl();

    public static UserService getInstance() {
        return userService;
    }

    @Override
    public SendMessage openUserMenu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("User menu");
        sendMessage.setChatId(message.getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        List<InlineKeyboardButton> inlinKeyboard = KeybordHelper.createInlinKeyboard(2);
        inlinKeyboard.get(0).setText("Buy Ticket");
        inlinKeyboard.get(0).setCallbackData("buy_ticket");

        inlinKeyboard.get(1).setText("My History");
        inlinKeyboard.get(1).setCallbackData("my_history");

        StorageOperation.updateUserState(message.getChatId().toString(), BotState.USER_OPEN_MENU);


        inlineKeyboardMarkup.setKeyboard(List.of(inlinKeyboard));
        return sendMessage;
    }

    @Override
    public EditMessageText openBuyTicket(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());

        editMessageText.setText("  11111  ");
        return editMessageText;
    }

    @Override
    public EditMessageText openMyHistory(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        //
        return editMessageText;
    }
}
