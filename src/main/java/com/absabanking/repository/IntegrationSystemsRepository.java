package com.absabanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationSystemsRepository extends JpaRepository<IntegrationSystems, Long> {
    IntegrationSystems findIntegrationSystemsById(long id);
}
