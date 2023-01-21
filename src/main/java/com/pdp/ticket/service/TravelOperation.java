package com.pdp.ticket.service;

import org.checkerframework.checker.units.qual.C;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TravelOperation {

    EditMessageText openTravel(CallbackQuery callbackQuery);
    EditMessageText addFromTravel(CallbackQuery callbackQuery);
    EditMessageText addToTravel(CallbackQuery callbackQuery);

    EditMessageText addDepartureTimeTravel(CallbackQuery callbackQuery);

    SendMessage addArrivalTimeTravel(Message message);
    SendMessage addBusTravel(Message message);
    EditMessageText addPriceForPerSeat(CallbackQuery callbackQuery);
    EditMessageText addCreatedTimeTravel(Message message);

}
