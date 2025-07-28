CREATE TYPE transcription_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED');

CREATE TABLE transcriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    audio_id UUID NOT NULL REFERENCES audio_files(id) ON DELETE CASCADE,
    external_job_id VARCHAR(255),
    status transcription_status NOT NULL DEFAULT 'PENDING',
    full_text TEXT,
    language_detected VARCHAR(10),
    confidence_score DECIMAL(5,4),
    processing_time_seconds INTEGER,
    error_message TEXT,
    webhook_received_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transcriptions_user_id ON transcriptions(user_id);
CREATE INDEX idx_transcriptions_audio_id ON transcriptions(audio_id);
CREATE INDEX idx_transcriptions_status ON transcriptions(status);
CREATE INDEX idx_transcriptions_external_job_id ON transcriptions(external_job_id);
CREATE INDEX idx_transcriptions_created_at ON transcriptions(created_at);
