package com.pdp.ticket.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface DestinationService {

    EditMessageText openDestination(CallbackQuery callbackQuery);

    EditMessageText addDestination(CallbackQuery callbackQuery);

    SendMessage createdNewDestination(Message message);

    EditMessageText showDestination(CallbackQuery callbackQuery);

}
