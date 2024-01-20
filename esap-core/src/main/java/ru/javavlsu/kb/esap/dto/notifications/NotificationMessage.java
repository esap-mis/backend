package ru.javavlsu.kb.esap.dto.notifications;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationMessage {
    private String to;
    private String title;
    private String body;
}
