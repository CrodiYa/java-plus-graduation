package ru.yandex.practicum.event.model.compilation;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.event.model.event.Event;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilation", schema = "event_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private Boolean pinned;

    @ManyToMany
    @JoinTable(
            name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @Builder.Default
    private Set<Event> events = new HashSet<>();
}
