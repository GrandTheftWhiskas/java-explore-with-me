package ru.practicum.central.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.central.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    User getUserById(Long userId);

    void deleteById(Long userId);

    @Query("select email from User")
    List<String> getEmail();

}
