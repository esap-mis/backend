package ru.javavlsu.kb.esap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AppointmentsCountByDayDTO {

    private LocalDate date;

    private long count;

    public AppointmentsCountByDayDTO(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }
}
