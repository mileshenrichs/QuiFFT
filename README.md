# QuiFFT
QuiFFT is a Fourier transform (FFT) library for digital audio files.  QuiFFT abstracts away the technical details of digital audio representation and wave mathematics and provides a delightfully simple interface for computing [Fourier transforms](https://en.wikipedia.org/wiki/Fourier_transform) in Java. 

For those not experienced in signal processing, implementing a program that performs an FFT on audio data can be daunting.  In addition to the interpretation of raw FFT output, it requires knowledge about discrete time sampling, signal windowing, smoothing, and overlap.  QuiFFT provides the opportunity to analyze signal frequencies without the prerequisite of obtaining intimate knowledge of signal processing concepts.

Your first FFT can be as easy as one line of code:
```
FFTResult fft = new QuiFFT("audio.mp3").fullFFT();
```
This code computes an FFT for a file called `audio.mp3` using QuiFFT's [default FFT parameters](#configuring-fft-parameters).  Computing a more customized FFT is easy!  Just add a few configuration methods to the `QuiFFT` object:
```
FFTResult fft = new QuiFFT("audio.mp3").windowSize(2048)
    .windowFunction(WindowFunction.BLACKMAN).windowOverlap(0.75).fullFFT();
```

---

### Table of Contents
- [__Getting Started__](#getting-started)
- [__Supported File Types__](#supported-file-types)
- [__QuiFFT Output Object Types__](#quifft-output-object-types)
    - [FFTFrame](#fftframe)
    - [FrequencyBin](#frequencybin)
    - [Full FFT](#full-fft)
    - [FFT Stream](#fft-stream)
- [__Configuring FFT Parameters__](#configuring-fft-parameters)
- [__JavaDoc and Code Examples__](#javadoc-and-code-examples)
    - [Basic FFT with default settings](#basic-fft-with-default-settings)
    - [FFT with customized parameters](#fft-with-customized-parameters)
    - [Music Frequency Spectrum Visualizer using FFTStream](#music-frequency-spectrum-visualizer-using-fftstream)
    
---

### Getting Started
QuiFFT can be installed from its Maven Central Repository:
```
<dependency>
    <groupId>org.quifft</groupId>
    <artifactId>quifft</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Supported File Types
`QuiFFT`'s constructor accepts __8- and 16-bit__ audio files with the following extensions:
- .wav
- .aiff
- .mp3

If a file not matching the above extensions or an audio file with a bit depth of 24 or 32 is provided, the constructor will throw an `UnsupportedAudioFileException`.

### QuiFFT Output Object Types
Interpreting QuiFFT's output isn't a difficult task.  QuiFFT has been designed with simplicity and ease of use in mind, so its output structures are named using basic signal processing vocabulary and include metadata fields to make it extremely clear what type of information each object represents.

QuiFFT offers two methods to compute FFTs, the most straightforward of which is [`fullFFT()`](#full-fft).  The Full FFT reads the entire audio file into memory, then computes and stores each FFT frame (output of FFT applied to a single sampling window), finally returning an array of all FFT frames for the entire audio file when it completes.  Typically the space complexity of this all-at-once computation won't be an issue, but if you're in a space-contrained environment or want to start using the results of each computed FFT frame right away, you'll want to take a look at [`fftStream()`](#fft-stream), which allows the computation of FFT frames one at a time.

#### FFTFrame
When computing a Fourier transform on an audio file, a prerequisite is to split the full-length waveform into multiple sampling windows (typically consisting of 2048, 4096, or 8192 samples each).  The FFT algorithm is then applied to each of these sampling windows separately, producing a sequence of __FFT frames__ that spans the duration of the entire audio file.

Therefore, the `FFTResult` object returned by `quiFFT.fullFFT()` includes an instance variable `fftResult.fftFrames` which points to an array of `FFTFrame` objects.  Here's what an `FFTFrame` looks like:
```
class FFTFrame {
    double frameStartMs;
    double frameEndMs;
    FrequencyBin[] bins;
}
``` 
The `frameStartMs` and `frameEndMs` variables indicate the range of time from the original audio file represented in the sampling window used as input to this frame's FFT computation.  The actual output of the Fourier transform is contained in the `bins` array.
#### FrequencyBin
The Fast Fourier Transform is based on the [Discrete Fourier Transform](https://en.wikipedia.org/wiki/Discrete_Fourier_transform), which separates the frequencies present in a signal into a finite set of discrete bins.  For example, a frequency bin from 300 Hz to 320 Hz will indicate the cumulative power of all frequencies in the signal that fall between 300 Hz and 320 Hz.

Each `FFTFrame` contains an array of these `FrequencyBin`s.  Here's what a single `FrequencyBin` looks like:
```
class FrequencyBin {
    double frequency;
    double amplitude;
}
```
The `frequency` variable indicates the start (lower) value of the bin range in Hertz.  The `amplitude` variable is what we're really interested in -- it represents the power of the frequencies within this bin from the time-domain sampling window.  For example, if you wanted to make a frequency spectrum visualizer, the `amplitude` values of each `FrequencyBin` would lie along the y-axis of the spectrum graph.  By default, amplitude values are in decibels (dB), but this can be changed through simple [configuration of the FFT parameters](#configuring-fft-parameters).

#### Full FFT
The Full FFT is not an output object of QuiFFT, but instead one of two methods for computing FFTs.  The Full FFT reads the entire audio file into memory, then computes and stores each FFT frame (output of FFT applied to a single sampling window), finally returning an array of all FFT frames for the entire audio file when it completes.  As mentioned in the introduction to this setting, if you fear this all-at-once computation will violate space constraints, it would be preferable to use the [FFT Stream](#fft-stream) method instead.

__The output object associated with a full FFT is `FFTResult`.__  Here's what it looks like:
```
class FFTResult {
    // metadata inherited from FFTOutputObject
    String fileName;
    long fileDurationMs;
    double frequencyResolution;
    double windowDurationMs;
    FFTParameters fftParameters;
    
    // output structure unique to FFTResult
    FFTFrame[] fftFrames;
}
```
FFT output is accessible through the `fftFrames` array, which is a field unique to `FFTResult`.

#### FFT Stream

### Configuring FFT Parameters
The Fourier transform can be viewed as a single function with a number of parameters that can be configured to produce optimal results based on characteristics of the audio sample and the requirements of the spectral analysis being performed.  QuiFFT uses method chaining to make configuration of these parameters straightforward.

Below is a reference for QuiFFT's configuration methods.  None of these parameters are required to be explicitly set -- default values are denoted by boldface.

| Method  | Description | Values | Constraints |
| --- | --- | --- | --- |
| `.windowSize()`  | Size of sampling windows from signal (number of samples per window) | Integers (i.e. `512`, `2048`, __`4096`__, `8192`) | Must be greater than 0, and if `numPoints` is not set, must be a power of 2 |
| `.windowFunction()`  | Window smoothing function to be applied to the time domain signal prior to computing FFT  | `WindowFunction.RECTANGULAR`, `WindowFunction.TRIANGULAR`, `WindowFunction.BARTLETT`, __`WindowFunction.HANNING`__, `WindowFunction.HAMMING`, `WindowFunction.BLACKMAN` | Cannot be null |
| `.windowOverlap()`  | Percentage by which consecutive windows will be overlapped (`0.50` = 50% overlap) | Decimal between `0.00` and `1.00` (__`0.50`__ by default) | Must be greater or equal to 0 and less than 1 |
| `.numPoints()`  | Number of points (N-point FFT); if set, each sampling window will be zero-padded up to the length designated by `numPoints`.  This parameter can be useful if you want to use a window size that isn't a power of 2 -- simply set `numPoints` to the next power of 2 greater than your desired window size. | Integers (equivalent to __`windowSize`__ by default) | Must be a power of 2 |
| `.dbScale()`  | Boolean indicating whether FFT output should be represented in decibels (dB).  On the dB scale, the highest possible amplitude a frequency bin can have is 0 dB (maximum energy) and the lowest is -100 dB (minimum energy) | __`true`__, `false` | If `true`, value of `normalized` doesn't matter because the decibels are, by definition, normalized |
| `.normalized()`  | Boolean indicating whether FFT output will be normalized such that each amplitude is in the range `0.00 - 1.00` where `1.00` represents the highest amplitude of any bin across the entire signal | __`true`__, `false` | - |

To get the current value of any of the above parameters from the `QuiFFT` object, simply call the configuration method without providing an argument.

### JavaDoc and Code Examples
See QuiFFT's JavaDoc on its website: https://www.quifft.org/javadoc

---

Below are some code examples that make use of QuiFFT's functionality.  All of these examples come from the [quifft-examples](https://github.com/mileshenrichs/quifft-examples) repository.

#### Basic FFT with default settings
[quifft-examples/yourfirstfft]()

#### FFT with customized parameters
- FFT with rectangular windows of length 1024 samples and 75% window overlap
- FFT whose amplitude values are scaled linearly (as opposed to logarithmic dB) between 0 and 1

[quifft-examples/customizedparams]()

#### Music Frequency Spectrum Visualizer using FFTStream
This spectrum visualizer renders an animated graph of the frequency content of full-length MP3 files using QuiFFT's `FFTStream` and [JFreeChart](https://github.com/jfree/jfreechart).

[quifft-examples/spectrum]()