package com.transvoz.module.audio.repository;

import com.transvoz.module.audio.entity.Audio;
import com.transvoz.shared.enums.AudioStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AudioRepository extends JpaRepository<Audio, UUID> {
    Page<Audio> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    Optional<Audio> findByIdAndUserId(UUID id, UUID userId);
    
    @Query("SELECT a FROM Audio a WHERE a.user.id = ?1 AND a.status = ?2")
    List<Audio> findByUserIdAndStatus(UUID userId, AudioStatus status);
    
    @Query("SELECT COUNT(a) FROM Audio a WHERE a.user.id = ?1")
    long countByUserId(UUID userId);
    
    @Query("SELECT SUM(a.fileSize) FROM Audio a WHERE a.user.id = ?1")
    Long sumFileSizeByUserId(UUID userId);
}
