package ru.yandex.practicum.ewm.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.analyzer.mapper.EventSimilarityMapper;
import ru.yandex.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.yandex.practicum.ewm.stats.analyzer.repository.EventSimilarityRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {

    private final EventSimilarityRepository similarityRepository;
    private final EventSimilarityMapper similarityMapper;

    @Override
    public void updateSimilarities(List<EventSimilarityAvro> eventSimilarityAvroList) {
        List<EventSimilarity> similarities = eventSimilarityAvroList.stream()
                .map(similarityMapper::toEventSimilarity)
                .toList();

        similarityRepository.saveAll(similarities);
    }

    @Override
    public List<EventSimilarity> findByEventIdIn(Collection<Long> ids) {
        return similarityRepository.findByEventIdIn(ids);
    }

    @Override
    public List<EventSimilarity> findByEventIdOrderByScore(Long eventId, Integer limit) {
        return similarityRepository.findByEventIdOrderByScoreDesc(eventId, limit);
    }
}
