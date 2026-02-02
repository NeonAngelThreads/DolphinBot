package org.angellock.impl.util;

import com.sun.jna.Native;
import org.angellock.impl.win32terminal.AnsiEscapes;

public class CrossPlatformUtil {
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public static AnsiEscapes.Kernel32 loadWindowsKernel32() {
        if (!isWindows()) return null;
        return Native.load("kernel32", AnsiEscapes.Kernel32.class);
    }
}
