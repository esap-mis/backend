package ru.javavlsu.kb.esap.dto.notifications;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationMessage {
    private String to;
    private String title;
    private String body;
}
