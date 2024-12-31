# Run jextract to generate the java files for JAVA 22

JEXTRACT="../jextract-22/bin/jextract"
JEXTRACT_OUTPUT_DIR="jextract_output"
FLUIDSYNTH_SRC="fluidsynth-2.3.7-linux"

if [[ ! -d ../FluidSynthJava || ! -f $FLUIDSYNTH_SRC/build/include/fluidsynth.h ]]; then
    echo "This script must be run from FluidSynthJava top directory."  
    echo "FluidSynth source must have been previously built in $FLUIDSYNTH_SRC/build."  
    exit 1
fi

$JEXTRACT  \
  --include-dir /usr/include \
  --include-dir $FLUIDSYNTH_SRC/include \
  --output $JEXTRACT_OUTPUT_DIR \
  --target-package org.jjazz.fluidsynthjava.jextract \
  --library :libfluidsynth.so.3 \
  $FLUIDSYNTH_SRC/build/include/fluidsynth.h
  
  
  
  
  






