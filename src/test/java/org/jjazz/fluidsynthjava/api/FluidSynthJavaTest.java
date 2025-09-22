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

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Basic tests.
 */
public class FluidSynthJavaTest
{

    private static FluidSynthJava instance;

    public FluidSynthJavaTest()
    {
    }


    @BeforeClass
    public static void setUpClass()
    {
        assertTrue(FluidSynthJava.isLibrariesLoadedOk());
        instance = new FluidSynthJava();
        try
        {
            instance.open(true);
        } catch (FluidSynthException ex)
        {
            System.getLogger(FluidSynthJavaTest.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @AfterClass
    public static void tearDownClass()
    {
        if (instance != null && instance.isOpen())
        {
            instance.close();
        }
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }


    @Test
    public void testGetFluidSynthVersion()
    {
        System.out.println("getFluidSynthVersion");
        String expResult = "2.3.0";
        String result = instance.getFluidSynthVersion();
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckFluidSynthMinimumVersion()
    {
        System.out.println("checkFluidSynthMinimumVersion");
        boolean result = instance.checkFluidSynthMinimumVersion(2, 0, 0);
        assertEquals(true, result);
        result = instance.checkFluidSynthMinimumVersion(3, 0, 0);
        assertEquals(false, result);
    }

    /**
     * Test of sendShortMessage method, of class FluidSynthJava.
     */
    // @Test
    public void testSendShortMessage()
    {
        System.out.println("sendShortMessage");
        ShortMessage sm = null;
        instance.sendShortMessage(sm);
    }

    /**
     * Test of sendSysexMessage method, of class FluidSynthJava.
     */
    // @Test
    public void testSendSysexMessage()
    {
        System.out.println("sendSysexMessage");
        SysexMessage sm = null;
        instance.sendSysexMessage(sm);
    }


    @Test
    public void testPlayTestNotes()
    {
        System.out.println("playTestNotes");
        instance.playTestNotes();
    }


    @Test
    public void testIsPlatformSupported()
    {
        System.out.println("isPlatformSupported");
        boolean result = FluidSynthJava.isPlatformSupported();
        assertEquals(true, result);
    }

}
