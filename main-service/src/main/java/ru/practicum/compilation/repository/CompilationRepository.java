package ru.practicum.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.model.Compilation;

import java.util.List;


public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Compilation findCompilationById(Long id);

    @Query(value = "SELECT * FROM compilations " +
    "LIMIT ?1", nativeQuery = true)
    List<Compilation> findAll(int limit);

    void deleteById(Long id);
}
