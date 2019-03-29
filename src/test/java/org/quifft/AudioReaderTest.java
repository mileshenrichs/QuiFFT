package org.quifft;

import org.junit.Test;
import org.quifft.audioread.AudioReader;
import org.quifft.audioread.AudioReaderFactory;
import org.quifft.audioread.PCMReader;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class AudioReaderTest {

    private static File testAudio = TestUtils.getAudioFile("600hz-tone-3secs-mono.wav");

    @Test
    public void Should_Extract_Correct_Number_Of_Samples_From_WAV() throws IOException, UnsupportedAudioFileException {
        int SAMPLE_RATE = 44100;
        int AUDIO_DURATION_SEC = 3;
        int expectedSampleCount = SAMPLE_RATE * AUDIO_DURATION_SEC;

        AudioReader reader = new PCMReader(testAudio);
        int[] extractedSamples = reader.getWaveform();

        assertEquals(expectedSampleCount, extractedSamples.length);
    }

    @Test
    public void Instantiate_AudioReaderFactory_To_Make_Cobertura_Happy() {
        new AudioReaderFactory();
    }
}
