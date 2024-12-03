package ru.practicum.ewm.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilation) {
        log.info("==> create compilation: {}", newCompilation);
        CompilationDto compilation = compilationService.add(newCompilation);
        log.info("<== create compilation: {}", compilation);
        return compilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        log.info("==> delete compilation: compId = {}", compId);
        compilationService.delete(compId);
        log.info("<== delete compilation");
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable long compId, @RequestBody @Valid UpdateCompilationRequest newCompilation) {
        log.info("==> update compilation: compId = {}, newCompilation = {}", compId, newCompilation);
        newCompilation.setId(compId);
        CompilationDto compilation = compilationService.update(newCompilation);
        log.info("<== update compilation: {}", compilation);
        return compilation;
    }
}
