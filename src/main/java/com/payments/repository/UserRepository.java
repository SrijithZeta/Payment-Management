package com.payments.repository;
//new
import com.payments.model.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository {
    User save(User user);
//    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
}
