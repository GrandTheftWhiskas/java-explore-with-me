package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    User getUserById(Long id);

    Page<User> findByIdIn(List<Integer> ids, Pageable pageable);

    @Query("SELECT u FROM User AS u ")
    List<User> getAllUser();

    Page<User> findAll(Pageable pageable);

    void deleteById(Long id);

}
