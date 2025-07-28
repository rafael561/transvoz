CREATE TABLE speaker_diarizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transcription_id UUID NOT NULL REFERENCES transcriptions(id) ON DELETE CASCADE,
    speaker_id VARCHAR(50) NOT NULL,
    speaker_label VARCHAR(100),
    start_time DECIMAL(10,3) NOT NULL,
    end_time DECIMAL(10,3) NOT NULL,
    text TEXT NOT NULL,
    confidence_score DECIMAL(5,4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_speaker_diarizations_transcription_id ON speaker_diarizations(transcription_id);
CREATE INDEX idx_speaker_diarizations_speaker_id ON speaker_diarizations(speaker_id);
CREATE INDEX idx_speaker_diarizations_start_time ON speaker_diarizations(start_time);
CREATE INDEX idx_speaker_diarizations_end_time ON speaker_diarizations(end_time);
