package com.pdp.ticket.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface AdminService {

    SendMessage openAdminMenu(Message message);

    SendMessage backToMenu(CallbackQuery callbackQuery);

}
