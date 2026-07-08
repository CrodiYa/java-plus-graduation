package ru.yandex.practicum.ewm.stats.analyzer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "similarities")
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "event_A", nullable = false)
    private Long eventA;

    @Column(name = "event_B", nullable = false)
    private Long eventB;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "ts", nullable = false)
    private Instant timestamp;
}