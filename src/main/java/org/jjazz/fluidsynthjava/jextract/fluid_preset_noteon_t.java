// Generated by jextract

package org.jjazz.fluidsynthjava.jextract;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
public interface fluid_preset_noteon_t {

    int apply(jdk.incubator.foreign.MemoryAddress x0, jdk.incubator.foreign.MemoryAddress x1, int x2, int x3, int x4);
    static MemoryAddress allocate(fluid_preset_noteon_t fi) {
        return RuntimeHelper.upcallStub(fluid_preset_noteon_t.class, fi, constants$36.fluid_preset_noteon_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;III)I");
    }
    static MemoryAddress allocate(fluid_preset_noteon_t fi, ResourceScope scope) {
        return RuntimeHelper.upcallStub(fluid_preset_noteon_t.class, fi, constants$36.fluid_preset_noteon_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;III)I", scope);
    }
    static fluid_preset_noteon_t ofAddress(MemoryAddress addr) {
        return (jdk.incubator.foreign.MemoryAddress x0, jdk.incubator.foreign.MemoryAddress x1, int x2, int x3, int x4) -> {
            try {
                return (int)constants$36.fluid_preset_noteon_t$MH.invokeExact((Addressable)addr, x0, x1, x2, x3, x4);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}


