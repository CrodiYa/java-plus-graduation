package ru.yandex.practicum.participation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;

import java.time.Instant;

@Entity
@Table(name = "participation", schema = "participation_schema")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @CreationTimestamp
    private Instant created;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;
}
