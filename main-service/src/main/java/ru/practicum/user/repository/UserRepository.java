package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(Long id);

    @Query(value = "SELECT * FROM users AS u " +
    "WHERE u.id IN (?1) ", nativeQuery = true)
    Page<User> findByIdIn(List<Integer> ids, Pageable pageable);

    void deleteById(Long id);

}
