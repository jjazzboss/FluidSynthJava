// Generated by jextract

package org.jjazz.fluidsynthjava.jextract;

import java.lang.invoke.*;
import java.lang.foreign.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.foreign.ValueLayout.*;
import static java.lang.foreign.MemoryLayout.PathElement.*;

/**
 * {@snippet lang=c :
 * typedef void (*fluid_settings_foreach_option_t)(void *, const char *, const char *)
 * }
 */
public class fluid_settings_foreach_option_t {

    fluid_settings_foreach_option_t() {
        // Should not be called directly
    }

    /**
     * The function pointer signature, expressed as a functional interface
     */
    public interface Function {
        void apply(MemorySegment data, MemorySegment name, MemorySegment option);
    }

    private static final FunctionDescriptor $DESC = FunctionDescriptor.ofVoid(
        fluidsynth_h.C_POINTER,
        fluidsynth_h.C_POINTER,
        fluidsynth_h.C_POINTER
    );

    /**
     * The descriptor of this function pointer
     */
    public static FunctionDescriptor descriptor() {
        return $DESC;
    }

    private static final MethodHandle UP$MH = fluidsynth_h.upcallHandle(fluid_settings_foreach_option_t.Function.class, "apply", $DESC);

    /**
     * Allocates a new upcall stub, whose implementation is defined by {@code fi}.
     * The lifetime of the returned segment is managed by {@code arena}
     */
    public static MemorySegment allocate(fluid_settings_foreach_option_t.Function fi, Arena arena) {
        return Linker.nativeLinker().upcallStub(UP$MH.bindTo(fi), $DESC, arena);
    }

    private static final MethodHandle DOWN$MH = Linker.nativeLinker().downcallHandle($DESC);

    /**
     * Invoke the upcall stub {@code funcPtr}, with given parameters
     */
    public static void invoke(MemorySegment funcPtr,MemorySegment data, MemorySegment name, MemorySegment option) {
        try {
             DOWN$MH.invokeExact(funcPtr, data, name, option);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
}
