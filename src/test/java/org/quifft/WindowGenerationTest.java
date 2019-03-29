package org.quifft;

import org.junit.Test;
import org.quifft.params.WindowFunction;
import org.quifft.params.WindowFunctionGenerator;

import static org.junit.Assert.*;

public class WindowGenerationTest {

    // expected window values from MATLAB
    private static double[] triang14 = {0.071, 0.214 ,0.357, 0.500, 0.643, 0.786, 0.929, 0.929, 0.786, 0.643, 0.500, 0.357, 0.214, 0.071};
    private static double[] triang15 = {0.125, 0.250, 0.375, 0.500, 0.625, 0.750, 0.875, 1.0, 0.875, 0.750, 0.625, 0.500, 0.375, 0.250, 0.125};

    private static double[] bartlett14 = {0, 0.154, 0.308, 0.462, 0.615, 0.769, 0.923, 0.923, 0.769, 0.615, 0.462, 0.308, 0.154, 0};
    private static double[] bartlett15 = {0, 0.143, 0.286, 0.429, 0.571, 0.714, 0.857, 1.0, 0.857, 0.714, 0.571, 0.429, 0.286, 0.143, 0};

    private static double[] hann14 = {0, 0.057, 0.216, 0.440, 0.677, 0.874, 0.985, 0.985, 0.874, 0.677, 0.440, 0.216, 0.057, 0};
    private static double[] hann15 = {0, 0.050, 0.188, 0.389, 0.611, 0.812, 0.950, 1.0, 0.950, 0.812, 0.611, 0.389, 0.188, 0.050, 0};

    private static double[] hamming14 = {0.080, 0.133, 0.279, 0.485, 0.703, 0.884, 0.987, 0.987, 0.884, 0.703, 0.485, 0.279, 0.133, 0.080};
    private static double[] hamming15 = {0.080, 0.126, 0.253, 0.438, 0.642, 0.827, 0.954, 1.0, 0.954, 0.827, 0.642, 0.438, 0.253, 0.126, 0.080};

    private static double[] blackman14 = {0, 0.023, 0.108, 0.282, 0.537, 0.804, 0.976, 0.976, 0.804, 0.537, 0.282, 0.108, 0.023, 0};
    private static double[] blackman15 = {0, 0.019, 0.090, 0.237, 0.459, 0.714, 0.920, 1.0, 0.920, 0.714, 0.459, 0.237, 0.090, 0.019, 0};

    @Test
    public void Triangular_Window() {
        double[] window = WindowFunctionGenerator.generateWindow(14, WindowFunction.TRIANGULAR);
        assertArrayEquals(triang14, window, 0.001);

        window = WindowFunctionGenerator.generateWindow(15, WindowFunction.TRIANGULAR);
        assertArrayEquals(triang15, window, 0.001);
    }

    @Test
    public void Bartlett_Window() {
        double[] window = WindowFunctionGenerator.generateWindow(14, WindowFunction.BARTLETT);
        assertArrayEquals(bartlett14, window, 0.001);

        window = WindowFunctionGenerator.generateWindow(15, WindowFunction.BARTLETT);
        assertArrayEquals(bartlett15, window, 0.001);
    }

    @Test
    public void Hanning_Window() {
        double[] window = WindowFunctionGenerator.generateWindow(14, WindowFunction.HANNING);
        assertArrayEquals(hann14, window, 0.001);

        window = WindowFunctionGenerator.generateWindow(15, WindowFunction.HANNING);
        assertArrayEquals(hann15, window, 0.001);
    }

    @Test
    public void Hamming_Window() {
        double[] window = WindowFunctionGenerator.generateWindow(14, WindowFunction.HAMMING);
        assertArrayEquals(hamming14, window, 0.001);

        window = WindowFunctionGenerator.generateWindow(15, WindowFunction.HAMMING);
        assertArrayEquals(hamming15, window, 0.001);
    }

    @Test
    public void Blackman_Window() {
        double[] window = WindowFunctionGenerator.generateWindow(14, WindowFunction.BLACKMAN);
        assertArrayEquals(blackman14, window, 0.001);

        window = WindowFunctionGenerator.generateWindow(15, WindowFunction.BLACKMAN);
        assertArrayEquals(blackman15, window, 0.001);
    }

}
