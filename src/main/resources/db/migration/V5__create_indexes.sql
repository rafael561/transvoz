-- Índices adicionais para consultas analíticas e filtros frequentes.
-- Execute somente depois de V1..V4.

-- Melhorias em audio_files
CREATE INDEX IF NOT EXISTS idx_audio_files_mime_type
    ON audio_files(mime_type);

CREATE INDEX IF NOT EXISTS idx_audio_files_duration
    ON audio_files(duration_seconds);

-- Melhorias em speaker_diarizations
CREATE INDEX IF NOT EXISTS idx_speaker_diarizations_confidence
    ON speaker_diarizations(confidence_score);
