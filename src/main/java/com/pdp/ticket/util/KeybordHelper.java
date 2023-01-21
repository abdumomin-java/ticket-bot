package com.pdp.ticket.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeybordHelper {
    public static List<InlineKeyboardButton> createInlinKeyboard(int columnNumber) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 0; i < columnNumber; i++) {
            buttons.add(new InlineKeyboardButton());
        }
        return buttons;
    }
    public static List<List<InlineKeyboardButton>> createInlineKeyboard(int rowNumber, int columnNumber) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < rowNumber; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < columnNumber; j++) {
                row.add(new InlineKeyboardButton());
            }
            buttons.add(row);
        }
        return buttons;
    }
}
