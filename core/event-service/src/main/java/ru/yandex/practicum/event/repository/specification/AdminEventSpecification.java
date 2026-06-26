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

public class AdminEventSpecification extends BaseEventSpecification {

    private final List<Long> users;
    private final List<EventState> states;

    public AdminEventSpecification(List<Long> users, List<EventState> states, List<Long> categories,
                                   Instant rangeStart, Instant rangeEnd) {
        super(categories, rangeStart, rangeEnd);
        this.users = users;
        this.states = states;
    }

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(root.get("initiatorId").in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }

        return getPredicate(root, cb, predicates);
    }
}
