package com.transvoz.module.transcription.repository;

import com.transvoz.module.transcription.entity.Transcription;
import com.transvoz.shared.enums.TranscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TranscriptionRepository extends JpaRepository<Transcription, UUID> {
    Page<Transcription> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    Optional<Transcription> findByIdAndUserId(UUID id, UUID userId);
    
    Optional<Transcription> findByExternalJobId(String externalJobId);
    
    @Query("SELECT t FROM Transcription t WHERE t.user.id = ?1 AND t.status = ?2")
    List<Transcription> findByUserIdAndStatus(UUID userId, TranscriptionStatus status);
    
    @Query("SELECT COUNT(t) FROM Transcription t WHERE t.user.id = ?1")
    long countByUserId(UUID userId);
    
    List<Transcription> findByStatusIn(List<TranscriptionStatus> statuses);
}
