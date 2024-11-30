package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.data.domain.PageRollRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findBy(
            @RequestParam(defaultValue = "false") Boolean pinned, // Искать только закрепленные/не закрепленные подборки
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("==> findBy compilation: pinned = {}, from = {}, size = {}",
                pinned, from, size);
        List<CompilationDto> compilations = compilationService.findAll(pinned, PageRollRequest.of(from, size));
        log.info("<== findBy compilation: count = {}", compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable long compId) {
        log.info("==> findById compilation: compId = {}", compId);
        CompilationDto compilation = compilationService.findById(compId);
        log.info("<== findById compilation: {}", compilation);
        return compilation;
    }
}
