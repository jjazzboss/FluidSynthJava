ON LINUX

We need to build fluidsynth to get the appropriate fluidsynth.h (+ other includes) which will be analyzed by jextract.

- Download 2.3.7 fluidsynth source code and extract in FluidSynthJava top dir.

- Build: see fluidsynth build instructions
sudo apt install cmake
sudo apt install libglib2.0-0
sudo apt install libdlib2.0-dev
cd fluidsynth-2.3.7-linux
mkdir build; cd build
cmake ..
make

- Update run-jextract.sh variables if required (JEXTRACT, EXTRACT_OUTPUT_DIR etc.)

-./run-jextract.sh

- Inspect results in $JEXTRACT_OUTPUT_DIR/ and if ok copy source files to src/


In fluidsynth_h.java, SYMBOL_LOOKUP generated code depends on the --library option:
"jextract ... --library fluidsynth"  =>
static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("fluidsynth"), LIBRARY_ARENA)
            .or(SymbolLookup.loaderLookup())
            .or(Linker.nativeLinker().defaultLookup());
            
"jextract ... --library :libfluidsynth.so.3"  =>
static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.libraryLookup("libfluidsynth.so.3", LIBRARY_ARENA)
            .or(SymbolLookup.loaderLookup())
            .or(Linker.nativeLinker().defaultLookup());
            
"jextract ... --library :/usr/lib/x86_64-linux-gnu/libfluidsynth.so.3"  =>
 static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.libraryLookup("/usr/lib/x86_64-linux-gnu/libfluidsynth.so.3", LIBRARY_ARENA)
            .or(SymbolLookup.loaderLookup())
            .or(Linker.nativeLinker().defaultLookup());

