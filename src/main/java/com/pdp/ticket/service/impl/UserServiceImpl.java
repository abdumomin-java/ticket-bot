package com.pdp.ticket.service.impl;

import com.pdp.ticket.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

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
        return sendMessage;
    }
}
