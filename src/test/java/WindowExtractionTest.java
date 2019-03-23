import org.junit.BeforeClass;
import org.junit.Test;
import org.quifft.params.WindowFunction;
import org.quifft.sampling.SampleWindowExtractor;

import java.util.Arrays;

import static org.junit.Assert.*;

public class WindowExtractionTest {

    private static int[] exampleWave;

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

}
