package com.brewy.backend.repository;

import com.brewy.backend.model.TonalityResultEntity;
import com.brewy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TonalityResultRepository extends JpaRepository<TonalityResultEntity, Integer> {
}
