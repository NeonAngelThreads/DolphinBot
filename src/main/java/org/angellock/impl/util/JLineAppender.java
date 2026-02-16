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
 *    program. If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import org.angellock.impl.win32terminal.AnsiEscapes;
import org.jline.reader.LineReader;

import java.nio.charset.StandardCharsets;

public class JLineAppender extends AppenderBase<ILoggingEvent> {
    private Encoder<ILoggingEvent> encoder;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }

        byte[] byteArray = this.encoder.encode(eventObject);
        String message = new String(byteArray, StandardCharsets.UTF_8);

        LineReader reader = AnsiEscapes.getReader();
        if (reader != null) {
            if (message.endsWith("\n")) {
                message = message.substring(0, message.length() - 1);
            }
            if (message.endsWith("\r")) {
                 message = message.substring(0, message.length() - 1);
            }
            reader.printAbove(message);
        } else {
            System.out.print(message);
        }
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }
}
