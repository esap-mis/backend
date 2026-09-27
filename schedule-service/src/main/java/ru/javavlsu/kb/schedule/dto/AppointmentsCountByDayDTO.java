package ru.javavlsu.kb.schedule.dto;

import java.time.LocalDate;

public record AppointmentsCountByDayDTO(
        LocalDate date,
        long count
) {
}
