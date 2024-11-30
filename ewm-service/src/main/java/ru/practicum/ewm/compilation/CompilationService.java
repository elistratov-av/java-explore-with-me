package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {
    CompilationDto add(NewCompilationDto newCompilation);

    void delete(long id);

    CompilationDto update(UpdateCompilationRequest newCompilation);

    CompilationDto findById(long id);

    List<CompilationDto> findAll(Boolean pinned, Pageable pageable);
}
