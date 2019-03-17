package org.quifft.audioread;

import org.quifft.util.DurationUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Audio reader to extract waveform data from PCM-formatted files (WAV and AIFF)
 */
public class PCMReader implements AudioReader {

    // Audio file being read
    private File audio;

    // Input stream of audio file
    private AudioInputStream inputStream;

    /**
     * The construction of a PCMReader opens an {@link AudioInputStream} for the .wav file.
     * @param audio .wav file to be read
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file
     */
    public PCMReader(File audio) throws IOException, UnsupportedAudioFileException {
        this.audio = audio;
        getInputStream();

        System.out.println("New PCMReader created with audio format: " + inputStream.getFormat().toString());
    }

    @Override
    public int[] getWaveform() {
        List<Byte> bytes = getBytes();
        int frameSize = inputStream.getFormat().getFrameSize();

        int n = bytes.size() / frameSize;
        int[] wave = new int[n];
        int b = 0; // index into bytes list

        for(int i = 0; i < n; i++) {
            ByteBuffer bb = ByteBuffer.allocate(5);
            bb.order(inputStream.getFormat().isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            for(int j = 0; j < frameSize; j++) {
                bb.put(bytes.get(b++));
            }

            wave[i] = bb.getShort(0);
        }

        return wave;
    }

    @Override
    public File getFile() {
        return audio;
    }

    @Override
    public long getFileDurationMs() {
        return DurationUtil.getFileDurationMs(inputStream, audio);
    }

    @Override
    public AudioFormat getAudioFormat() {
        return inputStream.getFormat();
    }

    private List<Byte> getBytes() {
        try {
            AudioFormat audioFormat = inputStream.getFormat();
            int bytesPerFrame = audioFormat.getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                bytesPerFrame = 1;
            }

            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            List<Byte> fullSongBytes = new ArrayList<>();
            try {
                while (inputStream.read(audioBytes) != -1) {
                    for(byte audioByte : audioBytes) {
                        fullSongBytes.add(audioByte);
                    }
                }

                return fullSongBytes;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>();
    }

    private void getInputStream() throws IOException, UnsupportedAudioFileException {
        this.inputStream = AudioSystem.getAudioInputStream(audio);
    }

}
