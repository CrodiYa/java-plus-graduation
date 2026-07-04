package ru.yandex.practicum.event.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.yandex.practicum.event.model.event.Event;
import ru.yandex.practicum.interaction.dto.event.event.EventState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PublicEventSpecification extends BaseEventSpecification {

    private final String text;
    private final Boolean paid;

    public PublicEventSpecification(Instant rangeStart, Instant rangeEnd,
                                    Boolean paid, List<Long> categories, String text) {
        super(categories, rangeStart, rangeEnd);
        this.paid = paid;
        this.text = text;
    }

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (rangeStart == null && rangeEnd == null) {
            predicates.add(cb.greaterThan(root.get("eventDate"), Instant.now()));
        }

        if (text != null) {
            String pattern = "%" + text.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            ));
        }

        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

        return getPredicate(root, cb, predicates);
    }
}
