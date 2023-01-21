package com.pdp.ticket.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String chatId;
    private String phoneNumber;
    private Role role;
    private BigDecimal balance;
    private BotState botState;



}
