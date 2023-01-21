package com.pdp.ticket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class EditingTravel {

    private String id;
    private Travel travel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditingTravel that = (EditingTravel) o;
        return Objects.equals(id, that.id) && Objects.equals(travel, that.travel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, travel);
    }

    @Override
    public String toString() {
        return "EditingTravel{" +
                "id='" + id + '\'' +
                ", travel=" + travel +
                '}';
    }
}
