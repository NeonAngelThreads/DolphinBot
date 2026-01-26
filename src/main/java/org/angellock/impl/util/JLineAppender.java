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
