package com.icube.sim.tichu.users;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
    boolean existsByEmail(String email);
}
