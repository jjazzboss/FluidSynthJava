// Generated by jextract

package org.jjazz.fluidsynthjava.jextract;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
class constants$57 {

    static final FunctionDescriptor fluid_player_set_midi_tempo$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_INT
    );
    static final MethodHandle fluid_player_set_midi_tempo$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_player_set_midi_tempo",
        "(Ljdk/incubator/foreign/MemoryAddress;I)I",
        constants$57.fluid_player_set_midi_tempo$FUNC, false
    );
    static final FunctionDescriptor fluid_player_set_bpm$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_INT
    );
    static final MethodHandle fluid_player_set_bpm$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_player_set_bpm",
        "(Ljdk/incubator/foreign/MemoryAddress;I)I",
        constants$57.fluid_player_set_bpm$FUNC, false
    );
    static final FunctionDescriptor fluid_player_set_playback_callback$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_POINTER,
        C_POINTER
    );
    static final MethodHandle fluid_player_set_playback_callback$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_player_set_playback_callback",
        "(Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$57.fluid_player_set_playback_callback$FUNC, false
    );
    static final FunctionDescriptor fluid_player_set_tick_callback$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER,
        C_POINTER,
        C_POINTER
    );
    static final MethodHandle fluid_player_set_tick_callback$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_player_set_tick_callback",
        "(Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$57.fluid_player_set_tick_callback$FUNC, false
    );
    static final FunctionDescriptor fluid_player_get_status$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_player_get_status$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_player_get_status",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$57.fluid_player_get_status$FUNC, false
    );
    static final FunctionDescriptor fluid_player_get_current_tick$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_player_get_current_tick$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_player_get_current_tick",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$57.fluid_player_get_current_tick$FUNC, false
    );
}


