package com.kb.leggid.repository;

import com.kb.leggid.model.SubLeggid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubLeggidRepository  extends JpaRepository<SubLeggid, Long> {
    Optional<SubLeggid> findByName(String subredditName);
}
