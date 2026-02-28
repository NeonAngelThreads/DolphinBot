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

package org.angellock.impl.api;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Setter
@Getter
public class WebLogAppender extends AppenderBase<ILoggingEvent> {
    private Encoder<ILoggingEvent> encoder;
    private static final List<BiConsumer<String, String>> logListeners = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted() || logListeners.isEmpty()) {
            return;
        }

        String botName = eventObject.getMarkerList().get(0).getName();
        byte[] byteArray = this.encoder.encode(eventObject);
        String message = new String(byteArray, StandardCharsets.UTF_8);

        // 广播日志到所有监听器
        for (BiConsumer<String, String> listener : logListeners) {
            try {
                listener.accept(message, botName);
            } catch (Exception ignore) {
            }
        }
    }

    public static void addLogListener(BiConsumer<String, String> listener) {
        logListeners.add(listener);
    }

    public static void removeLogListener(BiConsumer<String, String> listener) {
        logListeners.remove(listener);
    }

}
