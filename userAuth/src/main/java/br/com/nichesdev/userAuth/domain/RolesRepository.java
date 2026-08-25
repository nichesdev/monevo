package br.com.nichesdev.userAuth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {
    Optional<RolesEntity> findByName(String name);
}
