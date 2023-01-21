package com.pdp.ticket.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Order {
    private UUID id;
    private User user;
    private Passenger passenger;
    private LocalDateTime createdTime;
    private Ticket ticket;
    private BigDecimal cost;
}
