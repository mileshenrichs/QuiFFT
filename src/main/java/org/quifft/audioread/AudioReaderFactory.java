package org.quifft.audioread;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Factory to produce an appropriate subclass of {@link AudioReader} depending on whether input file is MP3 or WAV/AIFF
 */
public class AudioReaderFactory {

    /**
     * Produces a {@link PCMReader} if given file is WAV or AIFF, {@link MP3Reader} if MP3, throws exception otherwise
     * @param audioFile audio file to read
     * @return the appropriate subclass of {@link AudioReader} based on file type
     * @throws IOException if an I/O exception arises during creation of audio reader
     * @throws UnsupportedAudioFileException if file isn't an MP3, WAV, or AIFF file
     */
    public static AudioReader audioReaderFor(File audioFile) throws IOException, UnsupportedAudioFileException {
        String fileExtension = getFileExtension(audioFile);

        switch (fileExtension) {
            case ".wav":
            case ".aiff":
                return new PCMReader(audioFile);
            case ".mp3":
                return new MP3Reader(audioFile);
            default:
                String msg = String.format("Cannot read file type %s; " +
                        "please provide a .wav, .aiff, or .mp3 file instead.", fileExtension);
                throw new UnsupportedAudioFileException(msg);
        }
    }

    /**
     * Extracts file extension from end of file name
     * @param file file from which to extract extension
     * @return extension of file, if exists (".wav", ".mp3", etc)
     * @throws UnsupportedAudioFileException if file name doesn't include an extension
     */
    private static String getFileExtension(File file) throws UnsupportedAudioFileException {
        String name = file.getName();
        if(!name.contains("."))
            throw new UnsupportedAudioFileException();

        return name.substring(name.lastIndexOf("."));
    }

}
