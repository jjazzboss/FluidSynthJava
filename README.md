# FluidSynthJava

This package lets you use the FluidSynth library directly from Java. 

FluidSynthJava provides a Java API for a subset of the native FluidSynth API, mainly the methods required to configure FluidSynth (load soundfont, adjust reverb and chorus) and
to send Midi messages to be rendered by FluidSynth. If you need more, it's not difficult to add new wrapper methods using the existing ones as model.

The connection to the FluidSynth native library is based on the new JEP 412 "Foreign Function &amp; Memory API" + jextract tool from Java 17.

This package is used by the [JJazzLab](https://github.com/jjazzboss/JJazzLab) application. 

## FluidSynth version

This is designed for FluidSynth >= 2.3

- Windows: the FluidSynth 2.3 native libraries (DLLs) are bundled in the package. 
- Mac/Linux: the FluidSynth native libraries (.dylib/.so) must have been previously installed in a standard way on the host.

## Use FluidSynthJava

For a simple example how to use FluidSynthJava, check out DemoApp in the [JJazzLab Toolkit](https://github.com/jjazzboss/JJazzLabToolkit).

### With Maven
```
<dependency>
    <groupId>org.jjazzlab</groupId>
    <artifactId>fluidsynthjava</artifactId>
    <version>0.3.0</version>
</dependency>
```
### With Gradle
```
compile 'org.jjazzlab:fluidsynthjava:0.3.0'
```









