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
package org.jjazz.fluidsynthjava;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Static methods to load the FluidSynth native libraries.
 * <p>
 * Windows native libraries are embedded in the package and extracted in a temporary directory. For other OS, FluidSynth must have been previously installed.
 */
public class FluidSynthNativeLoader
{

    // Static variables must be declared BEFORE the static block
    // IMPORTANT: libs order must be in reverse dependency order (e.g. libfluidsynth is last)
    private static final String[] LIBS_WIN_AMD64 = new String[]
    {
        "libs/win/amd64/libintl-8.dll",
        "libs/win/amd64/libglib-2.0-0.dll",
        "libs/win/amd64/libgthread-2.0-0.dll",
        "libs/win/amd64/libgobject-2.0-0.dll",
        "libs/win/amd64/libsndfile-1.dll",
        "libs/win/amd64/libgcc_s_sjlj-1.dll",
        "libs/win/amd64/libwinpthread-1.dll",
        "libs/win/amd64/libgomp-1.dll",
        "libs/win/amd64/libstdc++-6.dll",
        "libs/win/amd64/libinstpatch-2.dll",
        "libs/win/amd64/libfluidsynth-3.dll"
    };

    private static final String[] LIBS_WIN_X86 = new String[]
    {
    };

    private static final String COMMAND_LINE_PROPERTY_FLUIDSYNTH_LIB = "fluidsynthlib.path";
    private static final List<String> LIB_FILENAMES_LINUX = Arrays.asList("libfluidsynth.so.3", "libfluidsynth.so");
    private static final List<String> LIB_FILENAMES_MAC = Arrays.asList("libfluidsynth.3.dylib", "libfluidsynth.dylib");
    private static final List<String> LIB_DIRS_LINUX = Arrays.asList("/usr/lib/x86_64-linux-gnu", "/usr/lib", "/usr/lib64", "/usr/local/lib", "/lib");
    // Expect fluidsynth in the shared lib dir of the 3 MacOS package managers homebrew, fink (/opt/sw/lib), and MacPorts (/opt/local/lib)
    // homebrew uses different dirs depending on Intel or ARM processor: Intel=/usr/local/lib, M1=/opt/homebrew/lib.
    // 'brew --prefix' command shows prefix
    private static final List<String> LIB_DIRS_MAC = Arrays.asList("/usr/local/lib", "/opt/homebrew/lib", "/opt/sw/lib", "/opt/local/lib");
    private static final String PREF_LIB = "PrefLib";
    private static final Preferences prefs = Preferences.userRoot().node(FluidSynthNativeLoader.class.getName());
    private static final Logger LOGGER = Logger.getLogger(FluidSynthNativeLoader.class.getSimpleName());

    /**
     * Load the FluidSynth native libraries for any OS.
     *
     * @return True if load was succesfull
     */
    public static boolean loadNativeLibraries()
    {
        boolean error = false;

        if (Utilities.isWindows())
        {
            error = !loadNativeLibrariesWin();
        } else if (Utilities.isLinux() || Utilities.isMac())
        {
            error = !loadNativeLibrariesLinuxMac();
        } else
        {
            LOGGER.log(Level.WARNING, "loadNativeLibraries() Platform not supported os.name={0}",
                    System.getProperty("os.name", "XX").toLowerCase(Locale.ENGLISH));
        }

        if (!error)
        {
            LOGGER.info("loadNativeLibraries() Success");
        }

        return !error;
    }

    // =================================================================================================
    // Private methods
    // =================================================================================================    

    /**
     * Load native libraries on Linux or Mac.
     * <p>
     * Try various strategies, first using the optional COMMAND_LINE_PROPERTY_FLUIDSYNTH_LIB if set, then standard fluidsynth lib names in various standard
     * directories.<p>
     * ---
     * <p>
     * Can't manage to make System.loadLibrary("fluidsynth") to work, see
     * https://stackoverflow.com/questions/74604651/system-loadlibrary-cant-find-a-shared-library-on-linux
     * <p>
     * But System.load(path_to_fluidsynth.so.xx) works fine on linux and mac, and no need to explicitly load dependencies like on windows.
     *
     * @return true if success
     */
    private static boolean loadNativeLibrariesLinuxMac()
    {
        // Start with command line
        String lib = System.getProperty(COMMAND_LINE_PROPERTY_FLUIDSYNTH_LIB, null);
        if (lib != null)
        {
            if (quietLoadOrLoadLibrary(lib))
            {
                LOGGER.log(Level.INFO, "loadNativeLibrariesLinuxMac() using lib ={0}", lib);
                return true;
            }
            LOGGER.log(Level.WARNING, "loadNativeLibrariesLinuxMac() could not load library {0}", lib);
        }

        // If it worked before, we saved the path as a preference
        String prefLib = prefs.get(PREF_LIB, null);
        if (prefLib != null)
        {
            if (quietLoadOrLoadLibrary(prefLib))
            {
                LOGGER.log(Level.INFO, "loadNativeLibrariesLinuxMac() using pref lib ={0}", prefLib);
                return true;
            } else
            {
                prefs.remove(PREF_LIB);
            }
        }

        // Check various dirs/filenames
        var libDirs = getLinuxOrMacLibDirs();
        var libFilenames = getLinuxOrMacLibFilenames();
        for (var libFilename : libFilenames)
        {
            for (var libDir : libDirs)
            {
                if (!Files.isDirectory(Path.of(libDir)))
                {
                    continue;
                }
                String libPath = libDir + "/" + libFilename;
                if (quietLoad(libPath))
                {
                    LOGGER.log(Level.INFO, "loadNativeLibrariesLinuxMac() using libPath={0}", libPath);
                    prefs.put(PREF_LIB, libPath);
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Load native libraries on Windows.
     *
     * @return
     */
    private static boolean loadNativeLibrariesWin()
    {
        String[] libs = getWinFluidSynthLibs();

        if (libs.length == 0)
        {
            LOGGER.log(Level.SEVERE, "loadNativeLibrariesWin() No libs found for os={0} and arch={1}", new Object[]
            {
                System.getProperty("os.name"),
                System.getProperty("os.arch")
            });
            return false;
        }

        List<Path> libPaths;
        try
        {
            libPaths = extractWinLibsFromJar(libs); // Can't use InstalledFileLocator.getDefault() if we build a standard jar (would be easier with Netbeans nbm module and InstalledFileLocator API)
            assert libPaths.size() == libs.length : "libPaths.size()=" + libPaths.size() + " lib.length=" + libs.length;
        } catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "loadNativeLibrariesWin() Error extracting native libs ex={0}", ex.getMessage());
            return false;
        }


        // System.loadLibrary("libfluidsynth-3.dll") does not work within IDE (.dll file not found, maybe it works if deployed), but in addition there is the problem
        // of additional dependent dlls, which are loaded using native system => Need to manually load them in reverse dependence order.
        // Use "cycheck -v ./xxx.dll" to get the DLL dependency tree.
        boolean error = false;
        for (Path libPath : libPaths)
        {
            String strAbsPath = libPath.toAbsolutePath().toString();
            try
            {
                LOGGER.log(Level.FINE, "loadNativeLibrariesWin() loading {0}", strAbsPath);
                System.load(strAbsPath);
            } catch (SecurityException | UnsatisfiedLinkError ex)
            {
                LOGGER.log(Level.SEVERE, "loadNativeLibrariesWin() Can''t load lib={0}. Ex={1}", new Object[]
                {
                    strAbsPath, ex.getMessage()
                });
                error = true;
                break;
            }
        }
        return !error;
    }

    /**
     * Get the relative path for the required FluidSynth libs in *reverse* dependency order.
     * <p>
     *
     * @return Empty if no relevant libraries found.
     */
    static private String[] getWinFluidSynthLibs()
    {
        String[] res = new String[0];

        switch (System.getProperty("os.arch"))
        {
            case "amd64" ->
                res = LIBS_WIN_AMD64;
            case "x86" ->
                res = LIBS_WIN_X86;
        }

        return res;
    }

    /**
     * Where we might find the fluidsynth lib on the system.
     *
     * @return
     */
    private static List<String> getLinuxOrMacLibDirs()
    {
        var dirs = Utilities.isMac() ? LIB_DIRS_MAC : LIB_DIRS_LINUX;
        return dirs;
    }

    private static List<String> getLinuxOrMacLibFilenames()
    {
        return Utilities.isMac() ? LIB_FILENAMES_MAC : LIB_FILENAMES_LINUX;
    }

    /**
     * Extract the native libs into a temporary directory.
     * <p>
     * Needed because it's a jar package -with a nbm Netbeans package, which allows embedded files, we could use InstalledFileLocator instead (see JJazzLab
     * &lt;= 4.0.2).
     *
     * @param resourceLibs Resource path of each bundled native lib
     * @return The list of extracted files
     * @throws java.io.IOException
     */
    private static List<Path> extractWinLibsFromJar(String[] resourceLibs) throws IOException
    {
        List<Path> res = new ArrayList<>();
        int nbCopies = 0;


        // Create temporary directory -always the same so it can be reused
        Path tmpDir = Path.of(System.getProperty("java.io.tmpdir")).resolve("fluidsynthjava");
        if (!Files.isDirectory(tmpDir))
        {
            Files.createDirectory(tmpDir);
        }


        // Extract each resource lib if required
        for (String resourceLib : resourceLibs)
        {
            Path path = Path.of(resourceLib);
            Path libName = path.getFileName();
            Path tmpLibPath = tmpDir.resolve(libName);
            LOGGER.log(Level.FINE, "extractWinLibsFromJar() resourceLib={0} tmpLibPath={1}", new Object[]
            {
                resourceLib, tmpLibPath
            });

            if (!Files.exists(tmpLibPath))
            {
                try (InputStream is = FluidSynthNativeLoader.class.getResourceAsStream(resourceLib))
                {
                    Files.copy(is, tmpLibPath, StandardCopyOption.REPLACE_EXISTING);
                    nbCopies++;
                } catch (NullPointerException e)
                {
                    Files.deleteIfExists(tmpLibPath);
                    throw new IOException("Resource lib " + resourceLib + " was not found inside JAR");
                } catch (Exception e)
                {
                    Files.deleteIfExists(tmpLibPath);
                    throw e;
                }
            }

            res.add(tmpLibPath);
        }

        LOGGER.log(Level.INFO, "extractWinLibsFromJar() Copied {0} native library files to {1}", new Object[]
        {
            nbCopies, tmpDir
        });

        return res;
    }

    static private boolean quietLoadOrLoadLibrary(String lib)
    {
        if (lib.startsWith("/"))
        {
            return quietLoad(lib);
        } else
        {
            return quietLoadLibrary(lib);
        }
    }

    /**
     * Try to load a library using System.load().
     *
     * @param libPath absolute path to a library file
     * @return true if success.
     */
    static private boolean quietLoad(String libPath)
    {
        try
        {
            System.load(libPath);
        } catch (SecurityException | UnsatisfiedLinkError ex)
        {
            LOGGER.log(Level.FINE, "quietLoad() error {0}", ex.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Try to load a library using System.loadLibrary().
     *
     * @param libName library name (no lib prefix...)
     * @return true if success.
     */
    static private boolean quietLoadLibrary(String libName)
    {
        try
        {
            System.loadLibrary(libName);
        } catch (SecurityException | UnsatisfiedLinkError ex)
        {
            LOGGER.log(Level.FINE, "quietLoadlibrary() error {0}", ex.getMessage());
            return false;
        }

        return true;
    }

}
