import org.junit.Test;
import org.quifft.audioread.AudioReader;
import org.quifft.audioread.PCMReader;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static util.TestUtils.*;

public class AudioReaderTest {

    private static File testAudio = getAudioFile("600hz-tone-3secs.wav");

    @Test
    public void Should_Extract_Correct_Number_Of_Samples_From_WAV() throws IOException, UnsupportedAudioFileException {
        int SAMPLE_RATE = 44100;
        int AUDIO_DURATION_SEC = 3;
        int expectedSampleCount = SAMPLE_RATE * AUDIO_DURATION_SEC;

        AudioReader reader = new PCMReader(testAudio);
        int[] extractedSamples = reader.getWaveform();

        assertEquals(expectedSampleCount, extractedSamples.length);
    }
}
