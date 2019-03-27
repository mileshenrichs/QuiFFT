import org.junit.BeforeClass;
import org.junit.Test;
import org.quifft.audioread.AudioReader;
import org.quifft.audioread.AudioReaderFactory;
import org.quifft.params.WindowFunction;
import org.quifft.sampling.SampleWindowExtractor;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class WindowExtractionTest {

    private static int[] exampleWave;
    private static double[] hanning8 = {0, 0.19, 0.61, 0.95, 0.95, 0.61, 0.19, 0};

    @BeforeClass
    public static void createExampleWave() {
        exampleWave = new int[32];
        Arrays.fill(exampleWave, 32768);
    }

    @Test
    public void Should_Extract_Windows_Of_Correct_Length_And_Values() {
        final int WINDOW_SIZE = 16;

        SampleWindowExtractor windowExtractor =
                new SampleWindowExtractor(exampleWave, false, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0, 0);
        int[] window1 = windowExtractor.extractWindow(0);
        int[] window2 = windowExtractor.extractWindow(1);

        int[] expectedWindow = new int[16];
        Arrays.fill(expectedWindow, 32768);

        assertArrayEquals(expectedWindow, window1);
        assertArrayEquals(expectedWindow, window2);
    }

    @Test
    public void Should_Extract_Partial_Last_Window_Mono() {
        int[] wave = {1, 2, 3, 4, 5, 6};
        final int WINDOW_SIZE = 4;
        int[] expected1 = {1, 2, 3, 4};
        int[] expected2 = {5, 6, 0, 0};

        SampleWindowExtractor extractor =
                new SampleWindowExtractor(wave, false, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0, 0);

        assertArrayEquals(expected1, extractor.extractWindow(0));
        assertArrayEquals(expected2, extractor.extractWindow(1));
    }

    @Test
    public void Should_Extract_Partial_Last_Window_Stereo() {
        int[] wave = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6};
        final int WINDOW_SIZE = 4;
        int[] expected1 = {1, 2, 3, 4};
        int[] expected2 = {5, 6, 0, 0};

        SampleWindowExtractor extractor =
                new SampleWindowExtractor(wave, true, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0, 0);

        assertArrayEquals(expected1, extractor.extractWindow(0));
        assertArrayEquals(expected2, extractor.extractWindow(1));
    }

    @Test
    public void Should_Extract_Appropriate_Windows_With_Overlap() {
        int[] signal = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int WINDOW_SIZE = 4;
        double OVERLAP = 0.50;
        int[] expected1 = {1, 2, 3, 4};
        int[] expected2 = {3, 4, 5, 6};
        int[] expected3 = {5, 6, 7, 8};
        int[] expected4 = {7, 8, 9, 10};
        int[] expected5 = {9, 10, 11, 12};

        SampleWindowExtractor extractor = new SampleWindowExtractor(signal, false, WINDOW_SIZE,
                WindowFunction.RECTANGULAR, OVERLAP, 0);

        assertArrayEquals(expected1, extractor.extractWindow(0));
        assertArrayEquals(expected2, extractor.extractWindow(1));
        assertArrayEquals(expected3, extractor.extractWindow(2));
        assertArrayEquals(expected4, extractor.extractWindow(3));
        assertArrayEquals(expected5, extractor.extractWindow(4));
    }

    @Test
    public void Should_Extract_Appropriate_Windows_With_Overlap_And_Non_Integral_Signal_Length() {
        int[] signal = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int WINDOW_SIZE = 4;
        double OVERLAP = 0.50;
        int[] expected1 = {1, 2, 3, 4};
        int[] expected2 = {3, 4, 5, 6};
        int[] expected3 = {5, 6, 7, 8};
        int[] expected4 = {7, 8, 9, 10};
        int[] expected5 = {9, 10, 0, 0};

        SampleWindowExtractor extractor = new SampleWindowExtractor(signal, false, WINDOW_SIZE,
                WindowFunction.RECTANGULAR, OVERLAP, 0);

        assertArrayEquals(expected1, extractor.extractWindow(0));
        assertArrayEquals(expected2, extractor.extractWindow(1));
        assertArrayEquals(expected3, extractor.extractWindow(2));
        assertArrayEquals(expected4, extractor.extractWindow(3));
        assertArrayEquals(expected5, extractor.extractWindow(4));
    }

    @Test
    public void Should_Apply_Window_Functions_By_Multiplying_Coefficients_To_Signal() {
        int[] unitWave = new int[8];
        Arrays.fill(unitWave, 100);

        SampleWindowExtractor windowExtractor =
                new SampleWindowExtractor(unitWave, false,8, WindowFunction.HANNING, 0, 0);
        int[] extractedWindow = windowExtractor.extractWindow(0);

        assertEquals(hanning8.length, extractedWindow.length);
        for(int i = 0; i < extractedWindow.length; i++) {
            assertEquals((int) Math.round(100 * hanning8[i]), extractedWindow[i]);
        }
    }

    @Test
    public void Should_Ignore_Zero_Padding_When_Applying_Window() {
        int[] unitWave = new int[8];
        Arrays.fill(unitWave, 100);

        SampleWindowExtractor windowExtractor =
                new SampleWindowExtractor(unitWave, false,8, WindowFunction.HANNING, 0, 24);
        int[] extractedWindow = windowExtractor.extractWindow(0);

        assertEquals(32, extractedWindow.length);
        for(int i = 0; i < 8; i++) {
            assertEquals((int) Math.round(100 * hanning8[i]), extractedWindow[i]);
        }
        for(int i = 8; i < extractedWindow.length; i++) {
            assertEquals(0, extractedWindow[i]);
        }
    }

    @Test
    public void Should_Average_Channels_To_Convert_Stereo_To_Mono() {
        int[] stereoWave = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
        int[] expectedMonoWave = {5, 25, 45, 65, 85, 105};

        SampleWindowExtractor extractor =
            new SampleWindowExtractor(stereoWave, true, 6, WindowFunction.RECTANGULAR, 0, 0);
        int[] extractedWave = extractor.extractWindow(0);

        assertArrayEquals(expectedMonoWave, extractedWave);
    }

    @Test
    public void Should_Extract_Same_Window_From_WAV_Stereo_Signal_As_From_Mono() throws IOException, UnsupportedAudioFileException {
        File stereoFile = TestUtils.getAudioFile("500hz-tone-3secs-stereo.wav");
        File monoFile = TestUtils.getAudioFile("500hz-tone-3secs-mono.wav");
        AudioReader stereoReader = AudioReaderFactory.audioReaderFor(stereoFile);
        AudioReader monoReader = AudioReaderFactory.audioReaderFor(monoFile);

        SampleWindowExtractor stereoExtractor = new SampleWindowExtractor(stereoReader.getWaveform(),
                true, 1024, WindowFunction.RECTANGULAR, 0, 0);
        SampleWindowExtractor monoExtractor = new SampleWindowExtractor(monoReader.getWaveform(),
                false, 1024, WindowFunction.RECTANGULAR, 0, 0);

        int[] stereoWindow = stereoExtractor.extractWindow(1);
        int[] monoWindow = monoExtractor.extractWindow(1);

        assertArrayEquals(stereoWindow, monoWindow);
    }

    @Test
    public void Should_Extract_Same_Window_From_MP3_Stereo_Signal_As_From_Mono() throws IOException, UnsupportedAudioFileException {
        File stereoFile = TestUtils.getAudioFile("500hz-tone-3secs-stereo.mp3");
        File monoFile = TestUtils.getAudioFile("500hz-tone-3secs-mono.mp3");
        AudioReader stereoReader = AudioReaderFactory.audioReaderFor(stereoFile);
        AudioReader monoReader = AudioReaderFactory.audioReaderFor(monoFile);

        SampleWindowExtractor stereoExtractor = new SampleWindowExtractor(stereoReader.getWaveform(),
                true, 1024, WindowFunction.RECTANGULAR, 0, 0);
        SampleWindowExtractor monoExtractor = new SampleWindowExtractor(monoReader.getWaveform(),
                false, 1024, WindowFunction.RECTANGULAR, 0, 0);

        int[] stereoWindow = stereoExtractor.extractWindow(6);
        int[] monoWindow = monoExtractor.extractWindow(6);
        // allow tolerance of 5 (rounding after averaging isn't going to be perfect)
        for(int i = 0; i < 1024; i++) {
            assertTrue(Math.abs(stereoWindow[i] - monoWindow[i]) <= 5);
        }
    }

}
