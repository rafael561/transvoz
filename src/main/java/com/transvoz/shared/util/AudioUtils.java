package com.transvoz.shared.util;

import com.transvoz.shared.exception.TransVozException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class AudioUtils {

    private static final List<String> VALID_AUDIO_MIME_TYPES = Arrays.asList(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav",
            "audio/mp4", "audio/m4a", "audio/flac", "audio/aac",
            "audio/ogg", "audio/vorbis"
    );

    public static boolean isValidAudioMimeType(String mimeType) {
        return mimeType != null && VALID_AUDIO_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    public static Integer extractDuration(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Mp3Parser parser = new Mp3Parser();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            
            parser.parse(inputStream, new DefaultHandler(), metadata, parseContext);
            
            String duration = metadata.get("xmpDM:duration");
            if (duration != null) {
                try {
                    return (int) Double.parseDouble(duration);
                } catch (NumberFormatException e) {
                    log.warn("Could not parse duration: {}", duration);
                }
            }
            
            return null;
        } catch (Exception e) {
            log.warn("Could not extract audio duration: {}", e.getMessage());
            return null;
        }
    }

    public static String detectAudioFormat(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = FileUtils.getFileExtension(filename).toLowerCase();
            return switch (extension) {
                case "mp3" -> "MP3";
                case "wav" -> "WAV";
                case "m4a", "mp4" -> "M4A";
                case "flac" -> "FLAC";
                case "aac" -> "AAC";
                case "ogg" -> "OGG";
                default -> "UNKNOWN";
            };
        }
        return "UNKNOWN";
    }

    public static boolean needsConversion(String format) {
        return !"MP3".equalsIgnoreCase(format);
    }

    public static long estimateTranscriptionTime(Integer durationSeconds, long fileSize) {
        if (durationSeconds == null) {
            // Estimate based on file size (rough approximation)
            return Math.max(60, fileSize / (1024 * 1024) * 30); // 30 seconds per MB
        }
        // Usually transcription takes 10-30% of audio duration
        return Math.max(60, durationSeconds / 4);
    }
}
