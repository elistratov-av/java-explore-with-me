package ru.practicum.ewm.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.validation.ValidationException;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@Builder
public class SearchParams {
    String text; // текст для поиска в содержимом аннотации и подробном описании события
    Long[] categories; // список идентификаторов категорий в которых будет вестись поиск
    Boolean paid; // поиск только платных/бесплатных событий
    LocalDateTime rangeStart; // дата и время не раньше которых должно произойти событие
    LocalDateTime rangeEnd; // дата и время не позже которых должно произойти событие
    Boolean onlyAvailable; // только события у которых не исчерпан лимит запросов на участие
    EventSort sort; // Вариант сортировки: по дате события или по количеству просмотров; Available values : EVENT_DATE, VIEWS

    private void validate() {
        if (rangeStart != null && rangeEnd != null) {
            if (!rangeStart.isBefore(rangeEnd))
                throw new ValidationException("Дата окончания должна быть после даты начала");
        }
    }

    public Predicate toPredicate() {
        validate();

        BooleanExpression onlyPublished = QEvent.event.published.before(LocalDateTime.now());
        BooleanBuilder booleanBuilder = new BooleanBuilder(onlyPublished);
        if (StringUtils.isNoneBlank(text)) {
            BooleanExpression byAnnotation = QEvent.event.annotation.containsIgnoreCase(text);
            BooleanExpression byDescription = QEvent.event.description.containsIgnoreCase(text);
            booleanBuilder.and(byAnnotation.or(byDescription));
        }
        if (categories != null && categories.length > 0) {
            BooleanExpression byCategories = QEvent.event.category.id.in(categories);
            booleanBuilder.and(byCategories);
        }
        if (paid != null) {
            BooleanExpression byPaid = QEvent.event.paid.eq(paid);
            booleanBuilder.and(byPaid);
        }
        if (rangeStart == null && rangeEnd == null) {
            BooleanExpression afterCurrent = QEvent.event.eventDate.after(LocalDateTime.now());
            booleanBuilder.and(afterCurrent);
        } else if (rangeStart != null) {
            BooleanExpression byRangeStart = QEvent.event.eventDate.after(rangeStart);
            booleanBuilder.and(byRangeStart);
        } else {
            BooleanExpression byRangeEnd = QEvent.event.eventDate.before(rangeEnd);
            booleanBuilder.and(byRangeEnd);
        }
        if (onlyAvailable) {
            BooleanExpression isAvailable = QEvent.event.requests.size().lt(QEvent.event.participantLimit);
            booleanBuilder.and(isAvailable);
        }
        return booleanBuilder.getValue();
    }
}
