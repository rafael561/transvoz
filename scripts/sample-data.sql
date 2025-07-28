-- Sample data for development and testing
-- Run this after schema creation

-- Insert sample users
INSERT INTO users (id, email, password, first_name, last_name, role, is_active, email_verified) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'admin@transvoz.com', '$argon2id$v=19$m=16384,t=2,p=1$abcdefghijklmnop$abcdefghijklmnopqrstuvwxyz1234567890', 'Admin', 'User', 'ADMIN', true, true),
    ('550e8400-e29b-41d4-a716-446655440002', 'user1@transvoz.com', '$argon2id$v=19$m=16384,t=2,p=1$abcdefghijklmnop$abcdefghijklmnopqrstuvwxyz1234567890', 'John', 'Doe', 'USER', true, true),
    ('550e8400-e29b-41d4-a716-446655440003', 'user2@transvoz.com', '$argon2id$v=19$m=16384,t=2,p=1$abcdefghijklmnop$abcdefghijklmnopqrstuvwxyz1234567890', 'Jane', 'Smith', 'USER', true, true);

-- Insert sample audio files
INSERT INTO audio_files (id, user_id, original_filename, stored_filename, file_path, file_size, mime_type, duration_seconds, status) VALUES
    ('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440002', 'meeting_recording.mp3', 'audio_001.mp3', '/uploads/550e8400-e29b-41d4-a716-446655440002/audio_001.mp3', 15728640, 'audio/mpeg', 600, 'PROCESSED'),
    ('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440002', 'interview.wav', 'audio_002.wav', '/uploads/550e8400-e29b-41d4-a716-446655440002/audio_002.wav', 31457280, 'audio/wav', 1200, 'PROCESSED'),
    ('550e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440003', 'podcast_episode.mp3', 'audio_003.mp3', '/uploads/550e8400-e29b-41d4-a716-446655440003/audio_003.mp3', 94371840, 'audio/mpeg', 3600, 'PROCESSED');

-- Insert sample transcriptions
INSERT INTO transcriptions (id, user_id, audio_id, external_job_id, status, full_text, language_detected, confidence_score, processing_time_seconds) VALUES
    ('550e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440011', 'ext_job_001', 'COMPLETED', 'Hello everyone, welcome to our weekly meeting. Today we will discuss the project progress and next steps.', 'en', 0.95, 45),
    ('550e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440012', 'ext_job_002', 'COMPLETED', 'Thank you for taking the time to speak with us today. Could you tell us about your experience in software development?', 'en', 0.92, 90),
    ('550e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440013', 'ext_job_003', 'COMPLETED', 'Welcome to Tech Talk podcast. Today we have a special guest who will share insights about the future of AI.', 'en', 0.97, 180);

-- Insert sample speaker diarizations
INSERT INTO speaker_diarizations (id, transcription_id, speaker_id, speaker_label, start_time, end_time, text, confidence_score) VALUES
    -- Meeting recording speakers
    ('550e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440021', 'speaker_0', 'Speaker 1', 0.0, 15.5, 'Hello everyone, welcome to our weekly meeting.', 0.95),
    ('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440021', 'speaker_0', 'Speaker 1', 15.5, 35.2, 'Today we will discuss the project progress and next steps.', 0.94),
    
    -- Interview speakers  
    ('550e8400-e29b-41d4-a716-446655440033', '550e8400-e29b-41d4-a716-446655440022', 'speaker_0', 'Interviewer', 0.0, 12.3, 'Thank you for taking the time to speak with us today.', 0.93),
    ('550e8400-e29b-41d4-a716-446655440034', '550e8400-e29b-41d4-a716-446655440022', 'speaker_0', 'Interviewer', 12.3, 25.8, 'Could you tell us about your experience in software development?', 0.91),
    ('550e8400-e29b-41d4-a716-446655440035', '550e8400-e29b-41d4-a716-446655440022', 'speaker_1', 'Candidate', 25.8, 65.2, 'Certainly! I have been working as a software developer for over 5 years now.', 0.89),
    
    -- Podcast speakers
    ('550e8400-e29b-41d4-a716-446655440036', '550e8400-e29b-41d4-a716-446655440023', 'speaker_0', 'Host', 0.0, 18.7, 'Welcome to Tech Talk podcast.', 0.98),
    ('550e8400-e29b-41d4-a716-446655440037', '550e8400-e29b-41d4-a716-446655440023', 'speaker_0', 'Host', 18.7, 45.3, 'Today we have a special guest who will share insights about the future of AI.', 0.96),
    ('550e8400-e29b-41d4-a716-446655440038', '550e8400-e29b-41d4-a716-446655440023', 'speaker_1', 'Guest', 45.3, 120.8, 'Thank you for having me. AI is indeed transforming how we work and live.', 0.93);
