package com.pdp.ticket.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {

    SendMessage openUserMenu(Message message);
    EditMessageText openBuyTicket(CallbackQuery callbackQuery);
    EditMessageText openMyHistory(CallbackQuery callbackQuery);
}
