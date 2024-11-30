package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    User getUserById(Long id);

    @Query(value = "SELECT * FROM users AS u " +
    "WHERE u.id IN (?1) " +
    "LIMIT ?2 ", nativeQuery = true)
    List<User> findByIdIn(List<Integer> ids, int size);

    @Query("SELECT u FROM User AS u ")
    List<User> getAllUser();

    @Query(value = "SELECT * FROM users " +
    "LIMIT ?1 ", nativeQuery = true)
    List<User> findAll(int size);

    void deleteById(Long id);

}
