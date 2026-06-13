/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *    License for more details. You should have received a copy of the GNU General Public License along with this
 *    program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.win32terminal;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import org.angellock.impl.util.ConsoleTokens;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.angellock.impl.util.CrossPlatformUtil.loadWindowsKernel32;

public class AnsiEscapes {
    private static final int TERMINAL_PROCESSING = 0x0004;
    private static Terminal winTerminal;
    private static LineReader reader;

    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = loadWindowsKernel32();

        Pointer GetStdHandle(int nStdHandle);

        int GetConsoleMode(Pointer hConsoleInput, int[] lpMode);

        boolean SetConsoleMode(Pointer hConsoleOutput, int dwMode);
    }

    public static void enableAnsiSupport() {
        if (Kernel32.INSTANCE != null) {
            Pointer stdHandle = Kernel32.INSTANCE.GetStdHandle(-11);
            int[] consoleMode = new int[1];
            if (Kernel32.INSTANCE.GetConsoleMode(stdHandle, consoleMode) > 0) {
                int newMode = consoleMode[0] | TERMINAL_PROCESSING;
                Kernel32.INSTANCE.SetConsoleMode(stdHandle, newMode);
            } else {
                System.out.println("Failed to set console mode.");
            }
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "chcp", "65001").inheritIO();
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (IOException e) {
                System.out.println(ConsoleTokens.colorizeText("&eFailed to change and active page code to 65001.(UTF-8)"));
            } catch (InterruptedException ignore) {
            }
        }
        try {
            winTerminal = TerminalBuilder.builder()
                    .system(true)
                    .encoding(StandardCharsets.UTF_8)
                    .build();

            reader = LineReaderBuilder.builder()
                    .terminal(AnsiEscapes.getTerminal())
                    .parser(new DefaultParser())
                    .completer(new SystemTabCompleter())
                    .option(LineReader.Option.AUTO_LIST, true)
                    .option(LineReader.Option.LIST_PACKED, true)
                    .option(LineReader.Option.AUTO_MENU, true)
                    .option(LineReader.Option.MENU_COMPLETE, true)
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M> ")
                    .build();
        } catch (Exception e) {
            System.out.println("Could not enable ansi escapes: " + e.getMessage());
            winTerminal = null;
            reader = null;
        }
    }

    public static void printArt(String ARCHIVE_VERSION) {
        System.out.print(ConsoleTokens.colorizeText("\n\n" + "&l" +
                "&b /\\/|_____      _       _     _      ______       _    " + "&1 __  &9__&b__  \n" +
                "&b|/\\/|  _  \\    | |     | |   (_)     | ___ \\     | |   " + "&5  -- &9\\ \\ &b\\ \n" +
                "&b    | | | |___ | |_ __ | |__  _ _ __ | |_/ / ___ | |_  " + "&5   -- &9\\ \\ &b\\\n" +
                "&b    | | | / _ \\| | '_ \\| '_ \\| | '_ \\| ___ \\/ _ \\| __| " + "&d   -- &9/ / &b/\n" +
                "&b    | |/ / (_) | | |_) | | | | | | | | |_/ / (_) | |_  " + "&5  -- &9/ / &b/ \n" +
                "&b    |___/ \\___/|_| .__/|_| |_|_|_| |_\\____/ \\___/ \\__| " + "&1 -- &9/_/&b_/ \n" +
                "&b                 | |                                   \n" +
                "&b                 |_|                                 " + "&5VERSION  &a&l" + ARCHIVE_VERSION + "\n\n\n")
        );
    }

    public static String shiftVersionTags(String version){
        return ConsoleTokens.colorizeText(version
                .replace("ALPHA", "&cALPHA")
                .replace("BETA", "&6BETA")
                .replace("RELEASE", "&aRELEASE")
        );
    }

    public static Terminal getTerminal() {
        return winTerminal;
    }

    public static LineReader getReader() {
        return reader;
    }
}
