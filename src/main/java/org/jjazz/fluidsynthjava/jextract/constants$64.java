// Generated by jextract

package org.jjazz.fluidsynthjava.jextract;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
class constants$64 {

    static final FunctionDescriptor fluid_mod_set_source1$FUNC = FunctionDescriptor.ofVoid(
        C_POINTER,
        C_INT,
        C_INT
    );
    static final MethodHandle fluid_mod_set_source1$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_mod_set_source1",
        "(Ljdk/incubator/foreign/MemoryAddress;II)V",
        constants$64.fluid_mod_set_source1$FUNC, false
    );
    static final FunctionDescriptor fluid_mod_set_source2$FUNC = FunctionDescriptor.ofVoid(
        C_POINTER,
        C_INT,
        C_INT
    );
    static final MethodHandle fluid_mod_set_source2$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_mod_set_source2",
        "(Ljdk/incubator/foreign/MemoryAddress;II)V",
        constants$64.fluid_mod_set_source2$FUNC, false
    );
    static final FunctionDescriptor fluid_mod_set_dest$FUNC = FunctionDescriptor.ofVoid(
        C_POINTER,
        C_INT
    );
    static final MethodHandle fluid_mod_set_dest$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_mod_set_dest",
        "(Ljdk/incubator/foreign/MemoryAddress;I)V",
        constants$64.fluid_mod_set_dest$FUNC, false
    );
    static final FunctionDescriptor fluid_mod_set_amount$FUNC = FunctionDescriptor.ofVoid(
        C_POINTER,
        C_DOUBLE
    );
    static final MethodHandle fluid_mod_set_amount$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_mod_set_amount",
        "(Ljdk/incubator/foreign/MemoryAddress;D)V",
        constants$64.fluid_mod_set_amount$FUNC, false
    );
    static final FunctionDescriptor fluid_mod_get_source1$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_mod_get_source1$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_mod_get_source1",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$64.fluid_mod_get_source1$FUNC, false
    );
    static final FunctionDescriptor fluid_mod_get_flags1$FUNC = FunctionDescriptor.of(C_INT,
        C_POINTER
    );
    static final MethodHandle fluid_mod_get_flags1$MH = RuntimeHelper.downcallHandle(
        fluidsynth_h.LIBRARIES, "fluid_mod_get_flags1",
        "(Ljdk/incubator/foreign/MemoryAddress;)I",
        constants$64.fluid_mod_get_flags1$FUNC, false
    );
}


