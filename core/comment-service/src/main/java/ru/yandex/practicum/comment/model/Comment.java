package ru.yandex.practicum.comment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "comments", schema = "comment_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created")
    @CreationTimestamp
    private Instant created;

    @Column(name = "updated")
    private Instant updated;
}
