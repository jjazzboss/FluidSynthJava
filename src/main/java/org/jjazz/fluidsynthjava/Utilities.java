/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *  Copyright @2019 Jerome Lelasseux. All rights reserved.
 *
 *  This file is part of the JJazzLab software.
 *
 *  JJazzLab is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License (LGPLv3)
 *  as published by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  JJazzLab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JJazzLab.  If not, see <https://www.gnu.org/licenses/>
 *
 *  Contributor(s):
 */
package org.jjazz.fluidsynthjava;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.logging.Logger;

/**
 * Various convenience functions.
 */
public class Utilities
{

    private static final Logger LOGGER = Logger.getLogger(Utilities.class.getName());

    /**
     * Get the OS type.
     */
    private enum OSType
    {
        Windows, MacOS, Linux, Other;
        public static final OSType DETECTED;

        static
        {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin")))
            {
                DETECTED = OSType.MacOS;
            } else if (OS.contains("win"))
            {
                DETECTED = OSType.Windows;
            } else if (OS.contains("nux"))
            {
                DETECTED = OSType.Linux;
            } else
            {
                DETECTED = OSType.Other;
            }
        }
    }

    public static boolean isWindows()
    {
        return OSType.DETECTED.equals(OSType.Windows);
    }

    public static boolean isMac()
    {
        return OSType.DETECTED.equals(OSType.MacOS);
    }

    public static boolean isLinux()
    {
        return OSType.DETECTED.equals(OSType.Linux);
    }


    static public String toString(byte[] buf)
    {
        char[] chars = new char[buf.length];
        for (int i = 0; i < buf.length; i++)
        {
            chars[i] = (char) buf[i];
        }
        return String.valueOf(chars);

    }


    /**
     * Get each element toString() called, one per line.
     *
     * @param <K>
     * @param <V>
     * @param map If it's a NavigableMap, use its ascending order.
     * @return
     */
    public static <K, V> String toMultilineString(Map<K, V> map)
    {
        Preconditions.checkNotNull(map);
        var joiner = new StringJoiner("\n", "[", "]");
        if (map instanceof NavigableMap nMap)
        {
            nMap.navigableKeySet().forEach(k -> joiner.add((k == null ? "null" : k.toString()) + " -> " + nMap.get(k)));
        } else
        {
            map.keySet().forEach(k -> joiner.add((k == null ? "null" : k.toString()) + " -> " + map.get(k)));
        }

        return joiner.toString();
    }

    /**
     * Get each element toString() called, one per line.
     *
     * @param list
     * @return
     */
    public static String toMultilineString(Collection<?> list)
    {
        Preconditions.checkNotNull(list);
        var joiner = new StringJoiner("\n", "[", "]");
        list.forEach(e -> joiner.add(e == null ? "null" : e.toString()));
        return joiner.toString();
    }

    /**
     * Get each element toString() called, one per line.
     *
     * @param array
     * @return
     */
    public static <T> String toMultilineString(T array[])
    {
        return toMultilineString(Arrays.asList(array));
    }


    // ========================================================================
    // Private methods
    // ========================================================================
}
