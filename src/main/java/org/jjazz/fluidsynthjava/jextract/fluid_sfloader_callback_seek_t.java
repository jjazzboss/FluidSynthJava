// Generated by jextract

package org.jjazz.fluidsynthjava.jextract;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
public interface fluid_sfloader_callback_seek_t {

    int apply(jdk.incubator.foreign.MemoryAddress x0, long x1, int x2);
    static MemoryAddress allocate(fluid_sfloader_callback_seek_t fi) {
        return RuntimeHelper.upcallStub(fluid_sfloader_callback_seek_t.class, fi, constants$30.fluid_sfloader_callback_seek_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;JI)I");
    }
    static MemoryAddress allocate(fluid_sfloader_callback_seek_t fi, ResourceScope scope) {
        return RuntimeHelper.upcallStub(fluid_sfloader_callback_seek_t.class, fi, constants$30.fluid_sfloader_callback_seek_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;JI)I", scope);
    }
    static fluid_sfloader_callback_seek_t ofAddress(MemoryAddress addr) {
        return (jdk.incubator.foreign.MemoryAddress x0, long x1, int x2) -> {
            try {
                return (int)constants$31.fluid_sfloader_callback_seek_t$MH.invokeExact((Addressable)addr, x0, x1, x2);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}


