package com.pdp.ticket.service.impl;

import com.pdp.ticket.enam.BotState;
import com.pdp.ticket.enam.Role;
import com.pdp.ticket.model.User;
import com.pdp.ticket.service.BotService;
import com.pdp.ticket.util.StorageOperation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BotServiceImpl implements BotService {
    private static final BotService botService = new BotServiceImpl();

    public static BotService getInstance() {
        return botService;
    }

    @Override
    public SendMessage askContactNumber(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("""
                <b>Welcome to FlixBus company</b> \uD83D\uDE8C
                To use our service, please share your contact
                """);
        sendMessage.setParseMode("HTML");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton button = new KeyboardButton();
        button.setText("Share Contact ☎️");
        button.setRequestContact(true);
        KeyboardRow buttons = new KeyboardRow();
        buttons.add(button);
        replyKeyboardMarkup.setKeyboard(List.of(buttons));
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        registerUser(message);
        return sendMessage;
    }

    private void registerUser(Message message) {
        List<User> users = StorageOperation.getUsers();
        User user = users.stream().filter(u -> u.getChatId().equals(message.getChatId().toString())).findFirst().orElse(null);
        if (user == null) {
            User newUser = new User(
                    UUID.randomUUID(),
                    message.getChatId().toString(),
                    null,
                    Role.USER,
                    BigDecimal.ZERO,
                    BotState.SHARE_CONTACT);
            users.add(newUser);
            StorageOperation.writeUsers(users);
        }
    }


    @Override
    public List<SendMessage> login(Message message) {
        List<SendMessage> sendMessages = new ArrayList<>();
        Contact contact = message.getContact();
        String phoneNumber = contact.getPhoneNumber();
        List<User> users = StorageOperation.getUsers();
        User user = users.stream()
                .filter(u -> u.getChatId().equals(message.getChatId().toString()))
                .findFirst()
                .orElse(new User());

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (phoneNumber.equals("+998973514949")) {
            user.setPhoneNumber(phoneNumber);
            user.setBotState(BotState.ADMIN_MENU);
            user.setRole(Role.ADMIN);
            sendMessage.setText("Welcome admin!");
            sendMessages.add(AdminServiceImpl.getInstance().openAdminMenu(message));
        } else {
            user.setPhoneNumber(phoneNumber);
            user.setBotState(BotState.USER_MENU);
            sendMessage.setText("Welcome user");
            sendMessages.add(UserServiceImpl.getInstance().openUserMenu(message));
        }
        StorageOperation.writeUsers(users);
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        sendMessages.add(sendMessage);
        return sendMessages;
    }

    @Override
    public SendMessage backToMenu(User user, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        if (user.getRole().equals(Role.ADMIN)) {
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.ADMIN_MENU);
            return AdminServiceImpl.getInstance().openAdminMenu(message);
        }
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.USER_MENU);
        return UserServiceImpl.getInstance().openUserMenu(message);
    }
}