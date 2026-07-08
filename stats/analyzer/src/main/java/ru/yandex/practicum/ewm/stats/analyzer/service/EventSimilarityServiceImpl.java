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
    public void saveSimilarities(List<EventSimilarityAvro> eventSimilarityAvroList) {
        for (EventSimilarityAvro avro : eventSimilarityAvroList) {
            similarityRepository.findByEventAAndEventB(avro.getEventA(), avro.getEventB())
                    .ifPresentOrElse(
                            es -> updateSimilarity(es, avro),
                            () -> similarityRepository.save(similarityMapper.toEventSimilarity(avro))
                    );
        }
    }

    private void updateSimilarity(EventSimilarity es, EventSimilarityAvro avro) {
        es.setScore(avro.getScore());
        es.setTimestamp(avro.getTimestamp());
        similarityRepository.save(es);
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
