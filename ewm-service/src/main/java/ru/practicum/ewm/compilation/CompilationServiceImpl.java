package ru.practicum.ewm.compilation;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto newCompilation) {
        List<Event> events = new ArrayList<>();
        if (newCompilation.getEvents() != null && !newCompilation.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilation.getEvents());
            if (newCompilation.getEvents().size() != events.size()) {
                throw new ValidationException("События не найдены");
            }
        }

        Compilation c = compilationMapper.map(newCompilation, events);
        Compilation compilation = compilationRepository.save(c);
        return compilationMapper.map(compilation);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не найдена"));
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest newCompilation) {
        Compilation oldCompilation = compilationRepository.findById(newCompilation.getId())
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + newCompilation.getId() + " не найдена"));

        if (newCompilation.getPinned() != null)
            oldCompilation.setPinned(newCompilation.getPinned());
        if (StringUtils.isNoneBlank(newCompilation.getTitle()))
            oldCompilation.setTitle(newCompilation.getTitle());
        if (newCompilation.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(newCompilation.getEvents());
            if (newCompilation.getEvents().size() != events.size()) {
                throw new ValidationException("События не найдены");
            }

            oldCompilation.getEvents().removeAll(oldCompilation.getEvents());
            oldCompilation.getEvents().addAll(events);
        }

        Compilation compilation = compilationRepository.save(oldCompilation);
        return compilationMapper.map(compilation);
    }

    @Override
    public CompilationDto findById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не найдена"));
        return compilationMapper.map(compilation);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Pageable pageable) {
        Slice<Compilation> compilations = (pinned != null) ?
                compilationRepository.findAllPinnedWithEvents(pinned, pageable) :
                compilationRepository.findAllWithEvents(pageable);
        return compilationMapper.map(compilations.getContent());
    }
}
