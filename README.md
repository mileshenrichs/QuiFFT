# QuiFFT


### Configuring FFT Parameters
The Fourier transform can be viewed as a single function with a number of parameters that can be configured to produce optimal results based on characteristics of the audio sample and the requirements of the spectral analysis being performed.  QuiFFT uses method chaining to make configuration of these parameters straightforward.

Below is a reference for QuiFFT's configuration methods.  Default values are denoted by boldface.

| Method  | Description | Values | Constraints |
| --- | --- | --- | --- |
| `.windowSize()`  | Size of sampling windows from signal (number of samples per window) | Integers (i.e. `512`, `2048`, __`4096`__, `8192`) | Must be greater than 0, and if `numPoints` is not set, must be a power of 2 |
| `.windowFunction()`  | Window smoothing function to be applied to the time domain signal prior to computing FFT  | `WindowFunction.RECTANGULAR`, `WindowFunction.TRIANGULAR`, `WindowFunction.BARTLETT`, __`WindowFunction.HANNING`__, `WindowFunction.HAMMING`, `WindowFunction.BLACKMAN` | Cannot be null |
| `.windowOverlap()`  | Percentage by which consecutive windows will be overlapped (`0.50` = 50% overlap) | Decimal between `0.00` and `1.00` (__`0.50`__ by default) | Must be greater or equal to 0 and less than 1 |
| `.numPoints()`  | Number of points (N-point FFT); if greater than `windowSize`, each sampling window will be zero-padded up to the length designated by `numPoints` | Integers (equivalent to __`windowSize`__ by default) | Must be a power of 2 |
| `.dbScale()`  | Boolean indicating whether FFT output should be represented in decibels (dB).  On the dB scale, the highest possible amplitude a frequency bin can have is 0 dB (maximum energy) and the lowest is -100 dB (minimum energy) | __`true`__, `false` | If `true`, value of `normalized` doesn't matter because the decibels are, by definition, normalized |
| `.normalized()`  | Boolean indicating whether FFT output will be normalized such that each amplitude is in the range `0.00 - 1.00` where `1.00` represents the highest amplitude of any bin across the entire signal | __`true`__, `false` | - |