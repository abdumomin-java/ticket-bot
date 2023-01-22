package com.pdp.ticket.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Ticket {
    private UUID id;
    private Travel travel;
    private BigDecimal price;
    private int seatNumber;
    private TicketStatus status;

}
