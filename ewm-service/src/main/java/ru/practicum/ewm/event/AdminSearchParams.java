package ru.practicum.ewm.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminSearchParams {
    private Long[] users; // список id пользователей, чьи события нужно найти
    private EventState[] states; // список состояний в которых находятся искомые события
    private Long[] categories; // список id категорий в которых будет вестись поиск
    private LocalDateTime rangeStart; // дата и время не раньше которых должно произойти событие
    private LocalDateTime rangeEnd; // дата и время не позже которых должно произойти событие

    public Predicate toPredicate() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (users != null && users.length > 0) {
            BooleanExpression byUsers = QEvent.event.initiator.id.in(users);
            booleanBuilder.and(byUsers);
        }
        if (states != null && states.length > 0) {
            BooleanExpression byStates = QEvent.event.state.in(states);
            booleanBuilder.and(byStates);
        }
        if (categories != null && categories.length > 0) {
            BooleanExpression byCategories = QEvent.event.category.id.in(categories);
            booleanBuilder.and(byCategories);
        }
        if (rangeStart != null) {
            BooleanExpression byRangeStart = QEvent.event.eventDate.after(rangeStart);
            booleanBuilder.and(byRangeStart);
        }
        if (rangeEnd != null) {
            BooleanExpression byRangeEnd = QEvent.event.eventDate.before(rangeEnd);
            booleanBuilder.and(byRangeEnd);
        }
        return booleanBuilder.getValue();
    }
}
