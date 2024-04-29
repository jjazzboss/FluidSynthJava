/*
 * 
 *   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *  
 *   Copyright @2019 Jerome Lelasseux. All rights reserved.
 * 
 *   This file is part of the JJazzLab software.
 *    
 *   JJazzLab is free software: you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License (LGPLv3) 
 *   as published by the Free Software Foundation, either version 3 of the License, 
 *   or (at your option) any later version.
 * 
 *   JJazzLab is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *  
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with JJazzLab.  If not, see <https://www.gnu.org/licenses/>
 *  
 *   Contributor(s): 
 * 
 */
package org.jjazz.fluidsynthjava.api;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.FLUID_OK;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.fluid_settings_copystr;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.fluid_settings_getint;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.fluid_settings_getnum;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.fluid_settings_setint;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.fluid_settings_setnum;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.fluid_settings_setstr;

/**
 * Settings of a FluidSynth instance.
 */
public class Settings
{

    private final MemoryAddress fluid_settings_ma;

    public Settings(MemoryAddress fluid_settings_ma)
    {
        this.fluid_settings_ma = fluid_settings_ma;
    }


    /**
     * Set a setting which use a String value on the native settings instance.
     *
     * @param setting
     * @param value
     *
     * @return True if operation was successful
     */
    public boolean set(String setting, String value)
    {
        try (var scope = ResourceScope.newConfinedScope())
        {
            var setting_seg = CLinker.toCString(setting, scope);
            var value_seg = CLinker.toCString(value, scope);
            return fluid_settings_setstr(fluid_settings_ma, setting_seg, value_seg) == FLUID_OK();
        }
    }

    /**
     * Set a setting which use a double value on the native Settings instance.
     *
     * @param setting
     * @param value
     * @return True if operation was successful
     */
    public boolean set(String setting, double value)
    {
        try (var scope = ResourceScope.newConfinedScope())
        {
            var setting_seg = CLinker.toCString(setting, scope);
            return fluid_settings_setnum(fluid_settings_ma, setting_seg, value) == FLUID_OK();
        }
    }

    /**
     * Set a setting which use an int value on the native Settings instance.
     *
     * @param setting
     * @param value
     * @return True if operation was successful
     */
    public boolean set(String setting, int value)
    {
        try (var scope = ResourceScope.newConfinedScope())
        {
            var setting_seg = CLinker.toCString(setting, scope);
            return fluid_settings_setint(fluid_settings_ma, setting_seg, value) == FLUID_OK();
        }
    }

    /**
     * Get a string value setting of the native Settings instance.
     *
     * @param setting
     * @return
     */
    public String getString(String setting)
    {
        try (var scope = ResourceScope.newConfinedScope())
        {
            var setting_seg = CLinker.toCString(setting, scope);
            var value_seg = SegmentAllocator.ofScope(scope).allocate(256);
            fluid_settings_copystr(fluid_settings_ma, setting_seg, value_seg, 256);
            return CLinker.toJavaString(value_seg);
        }
    }

    /**
     * Get an int value setting of the native Settings instance.
     *
     * @param setting
     * @return
     */
    public int getInt(String setting)
    {
        try (var scope = ResourceScope.newConfinedScope())
        {
            var setting_seg = CLinker.toCString(setting, scope);
            var value_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_INT, 0);
            fluid_settings_getint(fluid_settings_ma, setting_seg, value_seg);
            return value_seg.toIntArray()[0];
        }
    }

    /**
     * Get a double value setting from the native Settings instance.
     *
     * @param setting
     * @return
     */
    public double getDouble(String setting)
    {
        try (var scope = ResourceScope.newConfinedScope())
        {
            var setting_seg = CLinker.toCString(setting, scope);
            var value_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_DOUBLE, 0d);
            fluid_settings_getnum(fluid_settings_ma, setting_seg, value_seg);
            return value_seg.toDoubleArray()[0];
        }
    }

}
