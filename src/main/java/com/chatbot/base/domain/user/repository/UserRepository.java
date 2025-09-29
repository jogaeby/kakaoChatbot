package com.chatbot.base.domain.user.repository;

import com.chatbot.base.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserKey(String userKey);
}
