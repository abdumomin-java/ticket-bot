package com.pdp.ticket.service;

import com.pdp.ticket.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface BotService {

    SendMessage askContactNumber(Message message);

    List<SendMessage> login(Message message);

    SendMessage backToMenu(User user, Message message);
}
