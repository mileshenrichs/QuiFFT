package org.quifft.audioread;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Factory to produce an appropriate subclass of {@link AudioReader}
 * depending on whether the input file is in MP3 or WAV format.
 */
public class AudioReaderFactory {

    public static AudioReader audioReaderFor(File audioFile) throws IOException, UnsupportedAudioFileException {
        String fileExtension = getFileExtension(audioFile);

        switch(fileExtension) {
            case ".mp3":
                return new MP3Reader(audioFile);
            case ".wav":
                return new WAVReader(audioFile);
            default:
                throw new UnsupportedAudioFileException();
        }
    }

    private static String getFileExtension(File file) throws UnsupportedAudioFileException {
        String name = file.getName();
        if(!name.contains("."))
            throw new UnsupportedAudioFileException();

        return name.substring(name.lastIndexOf("."));
    }

}
