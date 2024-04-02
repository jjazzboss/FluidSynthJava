# FluidSynthJava

This package lets you use the FluidSynth library directly from Java. The connection to the FluidSynth native library is based on the new JEP 412 "Foreign Function &amp; Memory API" + jextract from Java 17.

The FluidSynthJava file provides a Java API for a subset of the FluidSynth API, mainly the methods required to use FluidSynth as a software synth receiving Midi messages. 

This package is used by the JJazzLab application.


### Configuration

This is designed for FluidSynth >= 2.3

- Windows: the FluidSynth 2.3 native libraries (DLLs) are bundled in the package. 
- Mac/Linux: the FluidSynth native libraries (.dylib/.so) must have been previously installed on the system.





