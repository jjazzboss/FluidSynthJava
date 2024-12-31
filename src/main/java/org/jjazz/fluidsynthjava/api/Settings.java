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


import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.*;

/**
 * Settings of a FluidSynth instance.
 */
public class Settings
{

    private final MemorySegment fluid_settings_ms;

    public Settings(MemorySegment fluid_settings_ms)
    {
        this.fluid_settings_ms = fluid_settings_ms;
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
        try (var arena = Arena.ofConfined())
        {
            var setting_ms = arena.allocateFrom(setting);
            var value_ms = arena.allocateFrom(value);
            return fluid_settings_setstr(fluid_settings_ms, setting_ms, value_ms) == FLUID_OK();
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
        try (var arena = Arena.ofConfined())
        {
            var setting_ms = arena.allocateFrom(setting);
            return fluid_settings_setnum(fluid_settings_ms, setting_ms, value) == FLUID_OK();
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
        try (var arena = Arena.ofConfined())
        {
            var setting_ms = arena.allocateFrom(setting);
            return fluid_settings_setint(fluid_settings_ms, setting_ms, value) == FLUID_OK();
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
        try (var arena = Arena.ofConfined())
        {
            var setting_ms = arena.allocateFrom(setting);
            var value_ms = arena.allocate(256);
            fluid_settings_copystr(fluid_settings_ms, setting_ms, value_ms, 256);
            return value_ms.getString(0);
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
        try (var arena = Arena.ofConfined())
        {
            var setting_ms = arena.allocateFrom(setting);
            var value_ms = arena.allocateFrom(C_INT, 0);
            fluid_settings_getint(fluid_settings_ms, setting_ms, value_ms);
            return value_ms.get(C_INT, 0);
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
        try (var arena = Arena.ofConfined())
        {
            var setting_ms = arena.allocateFrom(setting);
            var value_ms = arena.allocateFrom(C_DOUBLE, 0);
            fluid_settings_getnum(fluid_settings_ms, setting_ms, value_ms);
            return value_ms.get(C_DOUBLE, 0);
        }
    }

}
