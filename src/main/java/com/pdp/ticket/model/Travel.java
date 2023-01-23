package com.pdp.ticket.model;

import com.pdp.ticket.enam.TravelStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Travel {
    private UUID id;
    private Destination from;
    private Destination to;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Bus bus;
    private BigDecimal priceForPerSeat;
    private TravelStatus status;
    private LocalDateTime createdTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Travel travel = (Travel) o;
        return Objects.equals(id, travel.id) && Objects.equals(from, travel.from) && Objects.equals(to, travel.to) && Objects.equals(departureTime, travel.departureTime) && Objects.equals(arrivalTime, travel.arrivalTime) && Objects.equals(bus, travel.bus) && Objects.equals(priceForPerSeat, travel.priceForPerSeat) && status == travel.status && Objects.equals(createdTime, travel.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, departureTime, arrivalTime, bus, priceForPerSeat, status, createdTime);
    }

    @Override
    public String toString() {
        return "Travel{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", bus=" + bus +
                ", priceForPerSeat=" + priceForPerSeat +
                ", status=" + status +
                ", createdTime=" + createdTime +
                '}';
    }
}
