package ru.yandex.practicum.ewm.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.analyzer.mapper.UserActionMapper;
import ru.yandex.practicum.ewm.stats.analyzer.model.UserAction;
import ru.yandex.practicum.ewm.stats.analyzer.repository.UserActionRepository;
import ru.yandex.practicum.ewm.stats.util.ActionWeight;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;
    private final UserActionMapper userActionMapper;

    @Override
    public void saveUserAction(List<UserActionAvro> userActionAvroList) {
        for (UserActionAvro avro : userActionAvroList) {
            userActionRepository.findByUserIdAndEventId(avro.getUserId(), avro.getEventId())
                    .ifPresentOrElse(
                            ua -> updateAction(ua, avro),
                            () -> userActionRepository.save(userActionMapper.toUserAction(avro))
                    );
        }
    }

    private void updateAction(UserAction ua, UserActionAvro avro) {
        ua.setActionType(userActionMapper.toActionType(avro.getActionType()));
        ua.setTimestamp(avro.getTimestamp());
        userActionRepository.save(ua);
    }

    @Override
    public List<UserAction> findByUserIdOrderByTimestamp(Long userId) {
        return userActionRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Override
    public Double calculateMaxRatingPerUserByEventId(Long eventId) {
        return userActionRepository.findAllByEventId(eventId).stream()
                .mapToDouble(ua -> ActionWeight.getWeight(ua.getActionType()))
                .sum();
    }
}
