import org.junit.BeforeClass;
import org.junit.Test;
import org.quifft.params.WindowFunction;
import org.quifft.sampling.SampleWindowExtractor;

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
                new SampleWindowExtractor(exampleWave, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0);
        int[] window1 = windowExtractor.extractWindow(0);
        int[] window2 = windowExtractor.extractWindow(1);

        int[] expectedWindow = new int[16];
        Arrays.fill(expectedWindow, 32768);

        assertArrayEquals(expectedWindow, window1);
        assertArrayEquals(expectedWindow, window2);
    }

    @Test
    public void Should_Apply_Window_Functions_By_Multiplying_Coefficients_To_Signal() {
        int[] unitWave = new int[8];
        Arrays.fill(unitWave, 100);

        SampleWindowExtractor windowExtractor =
                new SampleWindowExtractor(unitWave, 8, WindowFunction.HANNING, 0);
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
                new SampleWindowExtractor(unitWave, 8, WindowFunction.HANNING, 24);
        int[] extractedWindow = windowExtractor.extractWindow(0);

        assertEquals(32, extractedWindow.length);
        for(int i = 0; i < 8; i++) {
            assertEquals((int) Math.round(100 * hanning8[i]), extractedWindow[i]);
        }
        for(int i = 8; i < extractedWindow.length; i++) {
            assertEquals(0, extractedWindow[i]);
        }
    }

}
