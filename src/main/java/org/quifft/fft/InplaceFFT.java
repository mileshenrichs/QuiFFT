package org.quifft.fft;

/* ****************************************************************************
 *  Compilation:  javac InplaceFFT.java
 *  Execution:    java InplaceFFT n
 *  Dependencies: Complex.java
 *
 *  Compute the FFT of a length n complex sequence in-place.
 *  Uses a non-recursive version of the Cooley-Tukey FFT.
 *  Runs in O(n log n) time.
 *
 *  Reference:  Algorithm 1.6.1 in Computational Frameworks for the
 *  Fast Fourier Transform by Charles Van Loan.
 *
 *
 *  Limitations
 *  -----------
 *   -  assumes n is a power of 2
 *
 *
 ******************************************************************************/

/**
 * Class to perform FFT computation
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class InplaceFFT {

    // compute the FFT of x[], assuming its length is a power of 2
    public static void fft(Complex[] x) {

        // assume length is a power of 2
        int n = x.length;

        // bit reversal permutation
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
                Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int L = 2; L <= n; L = L+L) {
            for (int k = 0; k < L/2; k++) {
                double kth = -2 * k * Math.PI / L;
                Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                for (int j = 0; j < n/L; j++) {
                    Complex tao = w.times(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].minus(tao);
                    x[j*L + k]       = x[j*L + k].plus(tao);
                }
            }
        }
    }
}