// Generated by jextract

package org.jjazz.fluidsynthjava.jextract;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
class constants$51 {

    static final FunctionDescriptor fluid_midi_event_set_control$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_INT
    );
    static final MethodHandle fluid_midi_event_set_control$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_midi_event_set_control",
        "(Ljdk/incubator/foreign/MemoryAddress;I)I",
        constants$51.fluid_midi_event_set_control$FUNC, false
    );
    static final FunctionDescriptor fluid_midi_event_get_value$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_midi_event_get_value$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_midi_event_get_value",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$51.fluid_midi_event_get_value$FUNC, false
    );
    static final FunctionDescriptor fluid_midi_event_set_value$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_INT
    );
    static final MethodHandle fluid_midi_event_set_value$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_midi_event_set_value",
        "(Ljdk/incubator/foreign/MemoryAddress;I)I",
        constants$51.fluid_midi_event_set_value$FUNC, false
    );
    static final FunctionDescriptor fluid_midi_event_get_program$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_midi_event_get_program$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_midi_event_get_program",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$51.fluid_midi_event_get_program$FUNC, false
    );
    static final FunctionDescriptor fluid_midi_event_set_program$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_INT
    );
    static final MethodHandle fluid_midi_event_set_program$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_midi_event_set_program",
        "(Ljdk/incubator/foreign/MemoryAddress;I)I",
        constants$51.fluid_midi_event_set_program$FUNC, false
    );
    static final FunctionDescriptor fluid_midi_event_get_pitch$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_midi_event_get_pitch$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_midi_event_get_pitch",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$51.fluid_midi_event_get_pitch$FUNC, false
    );
}


