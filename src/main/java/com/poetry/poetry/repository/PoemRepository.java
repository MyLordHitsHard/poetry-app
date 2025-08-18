package com.poetry.poetry.repository;

import com.poetry.poetry.model.Poem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoemRepository extends JpaRepository<Poem, Long> {
    List<Poem> findByUserId(Long userId);
}
