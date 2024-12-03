package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.events")
    Slice<Compilation> findAllWithEvents(Pageable pageable);

    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.events WHERE c.pinned = ?1")
    Slice<Compilation> findAllPinnedWithEvents(boolean pinned, Pageable pageable);
}
