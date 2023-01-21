package com.pdp.ticket.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Bus {
    private UUID id;
    private String name;
    private String number;
    private int numberOfSeats;
    private boolean isActive;


    public Bus() {
    }
}
