package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}
