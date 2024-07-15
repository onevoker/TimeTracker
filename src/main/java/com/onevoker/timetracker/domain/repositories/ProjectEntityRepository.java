package com.onevoker.timetracker.domain.repositories;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectEntityRepository extends JpaRepository<ProjectEntity, Integer> {
    boolean existsByName(String name);

    Optional<ProjectEntity> findByName(String name);
}