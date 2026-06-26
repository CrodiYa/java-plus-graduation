package ru.yandex.practicum.interaction.dto.participation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequestCount {
    private Long eventId;
    private Integer count;

    public EventRequestCount() {
    }

    public EventRequestCount(Long eventId, Integer count) {
        this.eventId = eventId;
        this.count = count;
    }

    public EventRequestCount(Long eventId, Long count) {
        this.eventId = eventId;
        this.count = count != null ? count.intValue() : 0;
    }
}
