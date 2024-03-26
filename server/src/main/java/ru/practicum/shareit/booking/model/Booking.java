package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @NotNull
    @Column(name = "start_rent")
    private LocalDateTime start;
    @NotNull
    @Column(name = "end_rent")
    private LocalDateTime end;
    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingState status;
    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingState state;
}
