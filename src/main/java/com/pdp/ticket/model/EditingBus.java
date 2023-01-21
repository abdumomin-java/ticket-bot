package com.pdp.ticket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditingBus {

    private String chatId;

    private Bus bus;
}
