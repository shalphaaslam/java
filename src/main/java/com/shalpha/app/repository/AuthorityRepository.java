package com.shalpha.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shalpha.app.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
