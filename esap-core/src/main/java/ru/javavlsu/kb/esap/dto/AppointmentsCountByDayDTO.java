package ru.javavlsu.kb.esap.dto;

import java.time.LocalDate;

public record AppointmentsCountByDayDTO (
        LocalDate date,
        long count
) {}
