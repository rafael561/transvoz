package com.transvoz.module.transcription.repository;

import com.transvoz.module.transcription.entity.SpeakerDiarization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpeakerDiarizationRepository extends JpaRepository<SpeakerDiarization, UUID> {
    List<SpeakerDiarization> findByTranscriptionIdOrderByStartTime(UUID transcriptionId);
    
    @Query("SELECT DISTINCT sd.speakerId FROM SpeakerDiarization sd WHERE sd.transcription.id = ?1")
    List<String> findDistinctSpeakerIdsByTranscriptionId(UUID transcriptionId);
    
    void deleteByTranscriptionId(UUID transcriptionId);
    
    @Query("SELECT COUNT(DISTINCT sd.speakerId) FROM SpeakerDiarization sd WHERE sd.transcription.id = ?1")
    int countDistinctSpeakersByTranscriptionId(UUID transcriptionId);
}
