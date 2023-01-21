package com.pdp.ticket.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BusService {
    EditMessageText openBus(CallbackQuery callbackQuery);

    EditMessageText addBus(CallbackQuery callbackQuery);

    SendMessage askNumber(Message message);

    SendMessage askNumberOfSeats(Message message);

    SendMessage createNewBuss(Message message);

    EditMessageText showBuses(CallbackQuery callbackQuery);

}
