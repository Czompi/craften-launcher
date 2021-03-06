package de.craften.util;

import java.io.File;

/**
 * Simple OS Helper determines the current OS and cpu architecture.
 *
 * @author saschb2b
 * @author redbeard
 */
public final class OSHelper {
    private static String operatingSystem;
    private static OS os;
    private static final String pS = File.separator;
    private static final String[] mOsArch32 = {"x86", "i386", "i686"}, //32-bit
            mOsArch64 = {"x64", "ia64", "amd64"};                //64-bit

    private static void init() {
        if (operatingSystem == null) {
            operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Win")) {
                operatingSystem = "windows";
                os = OS.WINDOWS;
            } else if (operatingSystem.contains("Linux")) {
                operatingSystem = "linux";
                os = OS.LINUX;
            } else if (operatingSystem.contains("Mac")) {
                operatingSystem = "osx";
                os = OS.OSX;
            } else {
                os = OS.UNDEFINED;
            }
        }
    }

    /**
     * Returns true if Java is running in x86 (32 bit) version.
     *
     * @return
     */
    public static boolean isJava32bit() {
        String archInfo = System.getProperty("os.arch");

        if (archInfo != null && !archInfo.equals("")) {
            for (String aMOsArch32 : mOsArch32) {
                if (archInfo.equals(aMOsArch32)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if Java is running in x64 (64 bit) version.
     *
     * @return
     */
    public static boolean isJava64bit() {
        String archInfo = System.getProperty("os.arch");

        if (archInfo != null && !archInfo.equals("")) {
            for (String aMOsArch64 : mOsArch64) {
                if (archInfo.equals(aMOsArch64)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the OS processor architecture.
     *
     * @return 32 or 64
     */
    public static String getOSArch() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        String realArch;

        if (arch != null) {
            if (arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64")) {
                realArch = "64";
            } else {
                realArch = "32";
            }
        } else {
            if (wow64Arch != null && wow64Arch.endsWith("64")) {
                realArch = "64";
            } else {
                realArch = "32";
            }
        }
        return realArch;
    }

    /**
     * Returns the minecraft path for the current os system and creates the
     * path if it does not exist.
     *
     * @return
     */
    public static String getMinecraftPath() {
        init();

        String path = "";
        if (operatingSystem.equals("windows")) {
            path = System.getenv("APPDATA") + pS + ".minecraft" + pS;
            if (new File(path).exists()) {
                return path;
            }
        } else if (operatingSystem.equals("linux")) {
            path = System.getProperty("user.home") + pS + ".minecraft"
                    + pS;
            if (new File(path).exists()) {
                return path;
            }
        } else if (operatingSystem.equals("mac") || operatingSystem.equals("osx")) {
            path = System.getProperty("user.home") + pS + "Library" + pS
                    + "Application Support" + pS + "minecraft" + pS;
            if (new File(path).exists()) {
                return path;

            }
        }

        new File(path).mkdirs();
        return path;
    }

    /**
     * Returns the current {@see #OS} as an enum.
     * Can return undefined if the os does not match (Windows, Linux, Mac OSX)
     *
     * @return
     */
    public static OS getOSasEnum() {
        init();
        return os;
    }

    /**
     * Returns the operatins system as a String in lower case letters.
     *
     * @return
     */
    public static String getOSasString() {
        init();
        return operatingSystem.toLowerCase();
    }

    /**
     * Returns the current java path. The java programm for the current os
     * is added.
     * e.g. (path).javaw.exe in windows.
     *
     * @return
     */
    public static String getJavaPath() {
        init();
        String fs = File.separator;

        String path = System.getProperty("java.home") + fs + "bin" + fs;

        if (getOSasEnum() == OS.WINDOWS &&
                (new File(path + "javaw.exe").isFile())) {
            return path + "javaw.exe";
        }

        return path + "java";
    }
}
