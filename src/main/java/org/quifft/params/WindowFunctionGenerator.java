package org.quifft.params;

/**
 * Generates coefficients for various windowing functions based on
 * <a href="https://www.mathworks.com/help/dsp/ref/windowfunction.html">MATLAB implementations</a>
 */
public class WindowFunctionGenerator {

    /**
     * Generates coefficients for a window of specified length and type
     * @param N length of window (should be equal to number of samples taken from waveform)
     * @param windowType type of windowing function desired (i.e. Hanning, Blackman, etc)
     * @return coefficients for window of specified length and type
     * @see WindowFunction
     */
    public static double[] generateWindow(int N, WindowFunction windowType) {
        switch(windowType) {
            case TRIANGULAR:
                return triang(N);
            case BARTLETT:
                return bartlett(N);
            case HANNING:
                return hann(N);
            case HAMMING:
                return hamming(N);
            case BLACKMAN:
            default:
                return blackman(N);
        }
    }

    /**
     * Triangular window of size N
     * @see <a href="https://www.mathworks.com/help/signal/ref/triang.html">MATLAB reference</a>
     */
    private static double[] triang(int N) {
        double[] w = new double[N];

        int n = 0;
        if(N % 2 == 1) {
            for(; n < (N + 1) / 2; n++) {
                w[n] = (2.0 * (n + 1)) / (N + 1);
            }
            for(; n < N; n++) {
                w[n] = 2 - ((2.0 * (n + 1)) / (N + 1));
            }
        } else {
            for(; n < (N / 2); n++) {
                w[n] = (2.0 * (n + 1) - 1) / N;
            }
            for(; n < N; n++) {
                w[n] = 2 - ((2.0 * (n + 1) - 1) / N);
            }
        }

        return w;
    }

    /**
     * Bartlett window of size N
     * @see <a href="https://www.mathworks.com/help/signal/ref/bartlett.html">MATLAB reference</a>
     */
    private static double[] bartlett(int N) {
        double[] w = new double[N];

        int n = 0;
        for(; n <= (N - 1) / 2; n++) {
            w[n] = (2.0 * n) / (N - 1);
        }
        for(; n < N; n++) {
            w[n] = 2 - (2.0 * n) / (N - 1);
        }

        return w;
    }

    /**
     * Hanning window of size N
     * @see <a href="https://www.mathworks.com/help/signal/ref/hann.html">MATLAB reference</a>
     */
    private static double[] hann(int N) {
        double[] w = new double[N];

        for(int n = 0; n < N; n++) {
            w[n] = 0.5 * (1 - Math.cos(2 * Math.PI * (n / (N - 1.0))));
        }

        return w;
    }

    /**
     * Hamming window of size N
     * @see <a href="https://www.mathworks.com/help/signal/ref/hamming.html">MATLAB reference</a>
     */
    private static double[] hamming(int N) {
        double[] w = new double[N];

        for(int n = 0; n < N; n++) {
            w[n] = 0.54 - 0.46 * Math.cos(2 * Math.PI * (n / (N - 1.0)));
        }

        return w;
    }

    /**
     * Blackman window of size N
     * @see <a href="https://www.mathworks.com/help/signal/ref/blackman.html">MATLAB reference</a>
     */
    private static double[] blackman(int N) {
        double[] w = new double[N];

        for(int n = 0; n < N; n++) {
            w[n] = 0.42 - 0.5 * Math.cos((2 * Math.PI * n) / (N - 1)) + 0.08 * Math.cos((4 * Math.PI * n) / (N - 1));
        }

        return w;
    }

}
