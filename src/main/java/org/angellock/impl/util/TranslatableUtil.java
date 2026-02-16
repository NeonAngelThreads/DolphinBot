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

package org.angellock.impl.util;

import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.managers.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslatableUtil {
    private static final Logger log = LoggerFactory.getLogger("EventLogger");
    public static ResourceBundle bundle;

    public TranslatableUtil() {
        this(ConfigManager.getCoreSettings().getLanguage());
    }

    public TranslatableUtil(Locale locale) {
        bundle = ResourceBundle.getBundle("locale", locale);
    }

    public static void infoTranslatableOf(EnumSystemEvents translatableEvent, Object... args) {
        log.info(getFormattedMessage(translatableEvent, args));
    }

    public static void warnTranslatableOf(EnumSystemEvents translatableEvent, Object... args) {
        log.warn(getFormattedMessage(translatableEvent, args));
    }

    public static void errorTranslatableOf(EnumSystemEvents translatableEvent, Object... args) {
        log.error(getFormattedMessage(translatableEvent, args));
    }

    public static String getFormattedMessage(EnumSystemEvents translatableEvent, Object... args) {

        String rawMessage = bundle.getString(translatableEvent.getSpaceName());

        MessageFormat formattedMessage = new MessageFormat(rawMessage);
        return ConsoleTokens.colorizeText(formattedMessage.format(args));
    }
}
