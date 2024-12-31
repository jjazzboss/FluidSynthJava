/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 *  Copyright @2022 Jerome Lelasseux. All rights reserved.
 *
 *  You can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License (LGPLv3) 
 *  as published by the Free Software Foundation, either version 3 of the License, 
 *  or (at your option) any later version.
 *
 *  Software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU Lesser General Public License for more details.
 * 
 *  Contributor(s): 
 */
package org.jjazz.fluidsynthjava.api;

import org.jjazz.fluidsynthjava.FluidSynthNativeLoader;
import com.google.common.base.Preconditions;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import jdk.incubator.foreign.*;
import org.jjazz.fluidsynthjava.Utilities;
import static org.jjazz.fluidsynthjava.jextract.fluidsynth_h.*;

/**
 * A Java wrapper of a FluidSynth instance.
 * <p>
 * Only a subset of the native FluidSynth API is made available, mainly the methods required to configure FluidSynth and send Midi messages for audio rendering.
 * But it's not difficult to support more API, replicating the already available methods.
 */
public final class FluidSynthJava
{

    // JJazzLab code was designed with FluidSynth 2.3.0 which corresponds to FluidSynth API version 3 (libfluidsynth3), which 
    // is in use since 2.2.0 (2.1.x used libfluidsynth2).
    // See https://jjazzlab.freeforums.net/thread/567/ubuntu-20-04-installation-errors?page=1&scrollTo=2232    
    private static final int MIN_FLUIDSYNTH_VERSION_MAJOR = 2;
    private static final int MIN_FLUIDSYNTH_VERSION_MINOR = 2;
    private static final int MIN_FLUIDSYNTH_VERSION_MICRO = 0;


    public static final String PROP_CHORUS = "propChorus";
    public static final String PROP_REVERB = "propReverb";
    public static final String PROP_GAIN = "propGain";
    private static final boolean LIBRARIES_LOADED_OK;

    private static final Logger LOGGER = Logger.getLogger(FluidSynthJava.class.getSimpleName());

    private MemoryAddress fluid_settings_ma;
    private MemoryAddress fluid_synth_ma;
    private MemoryAddress fluid_driver_ma;
    private Settings settings;
    private Reverb reverb;
    private Chorus chorus;
    private File lastLoadedSoundFontFile;
    private int lastLoadedSoundFontFileId = -1;
    private final transient PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this);

    /**
     * Load the DLLs upon startup
     */
    static
    {
        LIBRARIES_LOADED_OK = FluidSynthNativeLoader.loadNativeLibraries();
    }

    public static boolean isLibrariesLoadedOk()
    {
        return LIBRARIES_LOADED_OK;
    }

    /**
     * Create a JavaFluidSynth object.
     * <p>
     * Use open() to allocate the native resources.
     */
    public FluidSynthJava()
    {
    }

    /**
     * Create a JavaFluidSynth object from another one.
     * <p>
     * If jfs native resources are allocated, create the native resources initialized with the same values for: reverb, chorus, gain, soundfont file (if
     * loaded), synth.device-id.
     *
     * @param fsj               Must be already open
     * @param createAudioDriver If true create the associated audio driver
     * @throws org.jjazz.fluidsynthjava.api.FluidSynthException
     */
    public FluidSynthJava(FluidSynthJava fsj, boolean createAudioDriver) throws FluidSynthException
    {
        Preconditions.checkArgument(fsj.isOpen());

        fluid_settings_ma = new_fluid_settings();
        settings = new Settings(fluid_settings_ma);
        fluid_synth_ma = new_fluid_synth(fluid_settings_ma);
        if (fluid_synth_ma == null)
        {
            throw new FluidSynthException("Can't create native FluidSynth instance");
        }
        setGain(fsj.getGain());
        setReverb(fsj.getReverb());
        setChorus(fsj.getChorus());
        settings.set("synth.device-id", fsj.getSettings().getInt("synth.device-id"));

        if (createAudioDriver && fsj.getNativeAudioDriverInstance() != null)
        {
            fluid_driver_ma = new_fluid_audio_driver(fluid_settings_ma, fluid_synth_ma);
            // TODO copy driver settings! 
        }

        File f = fsj.getLastLoadedSoundFontFile();
        if (f != null)
        {
            try
            {
                lastLoadedSoundFontFileId = loadSoundFont(f);
            } catch (FluidSynthException ex)
            {
                close();
                throw ex;
            }
        }
    }

    /**
     * Allocate the native resources : settings, synth and audio driver.
     *
     * @param createAudioDriver If true create the associated audio driver
     * @throws org.jjazz.fluidsynthjava.api.FluidSynthException
     */
    public void open(boolean createAudioDriver) throws FluidSynthException
    {
        if (!LIBRARIES_LOADED_OK)
        {
            throw new FluidSynthException("FluidSynth libraries not loaded, please check the log messages");
        }

        if (isOpen())
        {
            return;
        }

        String version = getFluidSynthVersion();
        LOGGER.log(Level.INFO, "open() FluidSynth version={0}", version);

        if (!checkFluidSynthMinimumVersion(MIN_FLUIDSYNTH_VERSION_MAJOR, MIN_FLUIDSYNTH_VERSION_MINOR, MIN_FLUIDSYNTH_VERSION_MICRO))
        {
            String msg = "FluidSynth version is too old. Minimum is " + MIN_FLUIDSYNTH_VERSION_MAJOR + "." + MIN_FLUIDSYNTH_VERSION_MINOR + "." + MIN_FLUIDSYNTH_VERSION_MICRO;
            LOGGER.log(Level.WARNING, "open() {0}", msg);
            throw new FluidSynthException(msg);
        }

        fluid_settings_ma = new_fluid_settings();
        settings = new Settings(fluid_settings_ma);
        fluid_synth_ma = new_fluid_synth(fluid_settings_ma);
        if (fluid_synth_ma == null)
        {
            throw new FluidSynthException("Error creating native FluidSynth synth instance");
        }

        setDeviceIdForXGCompatibility();

        if (createAudioDriver)
        {
            fluid_driver_ma = new_fluid_audio_driver(fluid_settings_ma, fluid_synth_ma);
            if (fluid_driver_ma == null)
            {
                close();
                throw new FluidSynthException("Error creating native FluidSynth audio driver");
            }
        }

        LOGGER.info("open() Native FluidSynth instance initialized");
    }

    /**
     * A 3-part string like "2.2.1"
     *
     * @return
     */
    public String getFluidSynthVersion()
    {
        int maj, min, mic;
        try (var scope = ResourceScope.newConfinedScope())
        {
            var major_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_INT, 0);
            var minor_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_INT, 0);
            var micro_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_INT, 0);
            fluid_version(major_seg, minor_seg, micro_seg);
            maj = major_seg.toIntArray()[0];
            min = minor_seg.toIntArray()[0];
            mic = micro_seg.toIntArray()[0];
        }
        return maj + "." + min + "." + mic;
    }

    /**
     * Check that FluidSynth version is at least the specified version.
     *
     * @param major if min. version is "2.1.3" =&gt; 2
     * @param minor if min. version is "2.1.3" =&gt; 1
     * @param micro if min. version is "2.1.3" =&gt; 3
     * @return
     */
    public boolean checkFluidSynthMinimumVersion(int major, int minor, int micro)
    {
        String version = getFluidSynthVersion();
        String[] strs = version.split("\\.");
        int maj, min, mic;
        try
        {
            maj = Integer.parseInt(strs[0]);
            min = Integer.parseInt(strs[1]);
            mic = Integer.parseInt(strs[2]);
        } catch (NumberFormatException e)
        {
            LOGGER.log(Level.WARNING, "checkFluidSynthMinimumVersion() can''t parse version maj.min.mic: {0}", e.getMessage());
            return false;
        }

        boolean res = true;
        if (maj < major)
        {
            res = false;
        } else if (maj == major && (min < minor || (min == minor && mic < micro)))
        {
            res = false;
        }

        return res;
    }


    /**
     * Check if the default FluidSynth instance is opened.
     *
     * @return
     */
    public boolean isOpen()
    {
        return fluid_synth_ma != null;
    }

    /**
     * Close and release the native resources.
     */
    public void close()
    {
        if (fluid_driver_ma != null)
        {
            delete_fluid_audio_driver(fluid_driver_ma);
        }
        if (fluid_synth_ma != null)
        {
            delete_fluid_synth(fluid_synth_ma);
        }
        if (fluid_settings_ma != null)
        {
            delete_fluid_settings(fluid_settings_ma);
        }
        fluid_driver_ma = fluid_synth_ma = fluid_settings_ma = null;
        lastLoadedSoundFontFile = null;
        lastLoadedSoundFontFileId = -1;

        LOGGER.info("close() Native FluidSynth instance closed");
    }

    public MemoryAddress getNativeFluidSynthInstance()
    {
        return fluid_synth_ma;
    }

    public MemoryAddress getNativeSettingsInstance()
    {
        return fluid_settings_ma;
    }

    public MemoryAddress getNativeAudioDriverInstance()
    {
        return fluid_driver_ma;
    }

    public Settings getSettings()
    {
        return settings;
    }

    /**
     * The last successfully loaded soundfont file.
     *
     * @return Can be null
     * @see #loadSoundFont(java.io.File)
     */
    public File getLastLoadedSoundFontFile()
    {
        return lastLoadedSoundFontFile;
    }

    /**
     * The id of the last successfully loaded soundfont file.
     *
     * @return -1 if not available
     * @see #loadSoundFont(java.io.File)
     */
    public int getLastLoadedSoundFontFileId()
    {
        return lastLoadedSoundFontFileId;
    }

    /**
     * Send a ShortMessage to the native FluidSynth instance for audio rendering.
     *
     * @param sm
     */
    public void sendShortMessage(ShortMessage sm)
    {
        switch (sm.getCommand())
        {
            case ShortMessage.NOTE_ON ->
            {
                int vel = sm.getData2();
                if (vel > 0)
                {
                    fluid_synth_noteon(fluid_synth_ma, sm.getChannel(), sm.getData1(), vel);
                } else
                {
                    fluid_synth_noteoff(fluid_synth_ma, sm.getChannel(), sm.getData1());
                }
            }

            case ShortMessage.NOTE_OFF ->
                fluid_synth_noteoff(fluid_synth_ma, sm.getChannel(), sm.getData1());

            case ShortMessage.PROGRAM_CHANGE ->
                fluid_synth_program_change(fluid_synth_ma, sm.getChannel(), sm.getData1());

            case ShortMessage.CONTROL_CHANGE ->
                fluid_synth_cc(fluid_synth_ma, sm.getChannel(), sm.getData1(), sm.getData2());

            default ->
            {
                int status = sm.getStatus();
                if (status == ShortMessage.SYSTEM_RESET)
                {
                    fluid_synth_system_reset(fluid_synth_ma);
                }
            }
        }
    }

    /**
     * Send a SysexMessage to the native FluidSynth instance.
     * <p>
     * For XG ON sysex message to works, synth.device-id must be 16 (up to FluidSynth 2.3.0) ! See https://github.com/FluidSynth/fluidsynth/issues/1092
     *
     * @param sm
     */
    public void sendSysexMessage(SysexMessage sm)
    {
        byte[] data = sm.getData(); // Does not contain the leading byte 0xF0 

        // FluidSynth does not expect the leading 0xF0 nor the last 0xF7 
        byte[] fluidData = Arrays.copyOfRange(data, 0, data.length - 1);

        try (var scope = ResourceScope.newConfinedScope())
        {
            SegmentAllocator allocator = SegmentAllocator.ofScope(scope);
            var fluidData_ma = allocator.allocateArray(CLinker.C_CHAR, fluidData);
            var handled_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_INT, 0);
            fluid_synth_sysex(fluid_synth_ma,
                    fluidData_ma,
                    fluidData.length,
                    MemoryAddress.NULL, MemoryAddress.NULL, handled_seg, 0);
            int handled = handled_seg.toIntArray()[0];
        }

    }

    /**
     * Set gain for the native FluidSynth instance.
     *
     * @param gain 0 to 10
     */
    public void setGain(float gain)
    {
        float old = getGain();
        if (old != gain)
        {
            // Better to not call set_gain() if unchanged value, it seems it still impacts sound
            fluid_synth_set_gain(fluid_synth_ma, gain);
            pcs.firePropertyChange(PROP_GAIN, old, gain);
        }
    }

    /**
     * Get the gain of the native FluidSynth instance.
     * <p>
     * @return
     */
    public float getGain()
    {
        return fluid_synth_get_gain(fluid_synth_ma);
    }

    /**
     * Set the Reverb of the native synth instance.
     *
     * @param newReverb
     * @return True if reverb was successfully modified.
     */
    public boolean setReverb(Reverb newReverb)
    {
        Reverb old = reverb;
        if (Objects.equals(newReverb, old))
        {
            // Better to not call setSetting() if value unchanged, because it still changes the sound for a short time
            return true;
        }
        boolean b = settings.set("synth.reverb.damp", newReverb.damp());
        b &= settings.set("synth.reverb.level", newReverb.level());
        b &= settings.set("synth.reverb.room-size", newReverb.room());
        b &= settings.set("synth.reverb.width", newReverb.width());

        reverb = newReverb;
        pcs.firePropertyChange(PROP_REVERB, old, reverb);

        return b;
    }

    /**
     * Get the Reverb of the native synth instance.
     *
     * @return
     */
    public Reverb getReverb()
    {
        if (reverb != null)
        {
            // Reverb was explicitly set, just return it
            return reverb;
        }

        // Get data  from the synth        
        float damp = (float) settings.getDouble("synth.reverb.damp");
        float level = (float) settings.getDouble("synth.reverb.level");
        float room = (float) settings.getDouble("synth.reverb.room-size");
        float width = (float) settings.getDouble("synth.reverb.width");
        return new Reverb(null, room, damp, width, level);
    }

    /**
     * Set the Chorus of the native Synth instances.
     *
     * @param newChorus
     * @return True if chorus was successfully modified.
     */
    public boolean setChorus(Chorus newChorus)
    {
        Chorus old = chorus;
        if (Objects.equals(newChorus, old))
        {
            // Better to not call setSetting() even if unchanged value, because it still changes the sound for a short time
            return true;
        }
        boolean b = settings.set("synth.chorus.depth", newChorus.depth());
        b &= settings.set("synth.chorus.level", newChorus.level());
        b &= settings.set("synth.chorus.nr", newChorus.nr());
        b &= settings.set("synth.chorus.speed", newChorus.speed());
        b &= (fluid_synth_set_chorus_group_type(fluid_synth_ma, -1, newChorus.type()) == FLUID_OK());

        chorus = newChorus;
        pcs.firePropertyChange(PROP_CHORUS, old, chorus);

        return b;
    }

    /**
     * Get the Chorus of the native Synth instance.
     *
     * @return
     */
    public Chorus getChorus()
    {
        if (chorus != null)
        {
            // Chorus was explicitly set vis setChorus(Chorus), just return it
            return chorus;
        }

        // Get data  from the synth
        float depth = (float) settings.getDouble("synth.chorus.depth");
        float level = (float) settings.getDouble("synth.chorus.level");
        int nr = settings.getInt("synth.chorus.nr");
        float speed = (float) settings.getDouble("synth.chorus.speed");
        int type = 0;
        try (var scope = ResourceScope.newConfinedScope())
        {
            var value_seg = SegmentAllocator.ofScope(scope).allocate(CLinker.C_INT, 0);
            fluid_synth_get_chorus_group_type(fluid_synth_ma, -1, value_seg);
            type = value_seg.toIntArray()[0];
        }
        return new Chorus(null, nr, speed, depth, type, level);
    }

    /**
     * Generate a .wav file from midiFile.
     * <p>
     * From "Fast file renderer for non-realtime MIDI file rendering" https://www.fluidsynth.org/api/FileRenderer.html.
     *
     * @param midiFile The input Midi file
     * @param wavFile  The wav file to be created
     * @throws org.jjazz.fluidsynthjava.api.FluidSynthException
     */
    public void generateWavFile(File midiFile, File wavFile) throws FluidSynthException
    {
        if (!midiFile.canRead())
        {
            throw new FluidSynthException("Can't access midiFile=" + midiFile.getAbsolutePath());
        }
        String midiFilePath = midiFile.getAbsolutePath();
        String wavFilePath = wavFile.getAbsolutePath();

        LOGGER.log(Level.FINE, "generateWavFile() -- midiFile={0} wavFile={1}", new Object[]
        {
            midiFile.getAbsolutePath(), wavFile.getAbsolutePath()
        });

        // Create a synth copy
        FluidSynthJava synthCopy = new FluidSynthJava(this, false);

        // specify the file to store the audio to
        // make sure you compiled fluidsynth with libsndfile to get a real wave file
        // otherwise this file will only contain raw s16 stereo PCM
        check(synthCopy.getSettings().set("audio.file.name", wavFilePath), "Can't set audio.file.name");
        check(synthCopy.getSettings().set("audio.file.type", "wav"), "Can't set audio.file.type");
        // use number of samples processed as timing source, rather than the system timer
        check(synthCopy.getSettings().set("player.timing-source", "sample"), "Can't set player.timing-source");
        // Since this is a non-realtime scenario, there is no need to pin the sample data
        check(synthCopy.getSettings().set("synth.lock-memory", 0), "Can't set synth.lock-memory");    // 1 by default                    

        // Prepare the player
        MemoryAddress synth_ma = synthCopy.getNativeFluidSynthInstance();
        MemoryAddress fluid_player_ma = new_fluid_player(synth_ma);
        check(fluid_player_ma != null, "Can't create player");
        try (var scope = ResourceScope.newConfinedScope())
        {
            var midiPath_seg = CLinker.toCString(midiFilePath, scope);
            check(fluid_player_add(fluid_player_ma, midiPath_seg) == FLUID_OK(), "Can't set Midi file as player input");
        }
        check(fluid_player_play(fluid_player_ma) == FLUID_OK(), "Can't start player");

        // Render music to file using FluidSynth's own player
        boolean error = false;
        MemoryAddress renderer_ma = new_fluid_file_renderer(synth_ma);
        check(renderer_ma != null, "Can't create file renderer");
        while (fluid_player_get_status(fluid_player_ma) == FLUID_PLAYER_PLAYING())
        {
            // LOGGER.severe(" - playing...");
            if (fluid_file_renderer_process_block(renderer_ma) != FLUID_OK())
            {
                error = true;
                break;
            }
        }

        // just for sure: stop the playback explicitly and wait until finished
        check(fluid_player_stop(fluid_player_ma) == FLUID_OK(), "Can't stop player");
        check(fluid_player_join(fluid_player_ma) == FLUID_OK(), "Can't join player");
        delete_fluid_file_renderer(renderer_ma);
        delete_fluid_player(fluid_player_ma);
        synthCopy.close();

        if (error)
        {
            throw new FluidSynthException("Problem while generating wav file " + wavFile.getAbsolutePath());
        }

    }

    /**
     * Load a soundfont file in the native FluidSynth instance.
     *
     * @param f
     * @return The soundfont id
     * @throws FluidSynthException
     */
    public int loadSoundFont(File f) throws FluidSynthException
    {
        if (f == null)
        {
            throw new IllegalArgumentException("f=" + f);
        }

        var sfont_path_native = CLinker.toCString(f.getAbsolutePath(), ResourceScope.newImplicitScope());
        lastLoadedSoundFontFileId = fluid_synth_sfload(fluid_synth_ma,
                sfont_path_native,
                1); // 1: re-assign presets for all MIDI channels (equivalent to calling fluid_synth_program_reset())           

        if (lastLoadedSoundFontFileId == FLUID_FAILED())
        {
            String msg = "Loading soundfont failed f=" + f.getAbsolutePath();
            LOGGER.log(Level.SEVERE, "loadSoundFont() {0}", msg);
            lastLoadedSoundFontFile = null;
            throw new FluidSynthException(msg);
        }

        LOGGER.log(Level.INFO, "loadSoundFont() SoundFont successfully loaded {0}", f.getAbsolutePath());

        lastLoadedSoundFontFile = f;
        return lastLoadedSoundFontFileId;
    }

    /**
     * Unload a soundfont from the native FluidSynth instance.
     *
     * @param sfId A Soundfont id returned by loadSoundfont().
     * @throws FluidSynthException
     * @see #loadSoundFont(java.io.File)
     */
    public void unloadSoundfont(int sfId) throws FluidSynthException
    {
        if (fluid_synth_sfunload(fluid_synth_ma, sfId, 1) == FLUID_FAILED())
        {
            String msg = "Unloading soundfont id=" + sfId + " failed";
            LOGGER.log(Level.SEVERE, "unloadSoundfont() {0}", msg);
            throw new FluidSynthException(msg);
        }

    }

    /**
     * Play a few predefined notes for testing.
     */
    public void playTestNotes()
    {

        for (int i = 0; i < 12; i++)
        {
            int key = 60 + i;
            fluid_synth_noteon(fluid_synth_ma, 0, key, 80);
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(FluidSynthJava.class.getName()).log(Level.SEVERE, null, ex);
            }
            fluid_synth_noteoff(fluid_synth_ma, 0, key);
        }

    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        pcs.removePropertyChangeListener(l);
    }

    /**
     * Check if the current platform is supported.
     *
     * @return
     */
    static public boolean isPlatformSupported()
    {
        return Utilities.isWindows() || Utilities.isLinux() || Utilities.isMac();
    }

// =============================================================================================
// Private methods
// =============================================================================================    
    private void check(boolean b, String exceptionText) throws FluidSynthException
    {
        if (!b)
        {
            throw new FluidSynthException(exceptionText);
        }
    }


    /**
     * Set the synth device Id for XG System ON compatibility.
     * <p>
     * IMPORTANT: FluidSynth 2.3.0 (should be fixed in 2.3.1) expects a special XG System ON message (3rd byte is NOT 0001nnnn with n the deviceId), which
     * differs from the standard one See https://github.com/FluidSynth/fluidsynth/issues/1092 Changing the deviceId to 16 is a trick to make it react to the
     * standard XG System ON
     */
    private void setDeviceIdForXGCompatibility()
    {
        settings.set("synth.device-id", 16);
    }


// =============================================================================================
// STATIC Private methods
// ============================================================================================= 
}
