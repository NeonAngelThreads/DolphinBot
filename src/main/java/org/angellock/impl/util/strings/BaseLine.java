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

package org.angellock.impl.util.strings;

import org.jline.reader.ParsedLine;

import java.util.List;

public record BaseLine(String line, int cursor) implements ParsedLine {

    @Override
    public String word() {
        return this.line.substring(0, this.cursor);
    }

    @Override
    public int wordCursor() {
        return this.cursor;
    }

    @Override
    public int wordIndex() {
        return 0;
    }

    @Override
    public List<String> words() {
        return null;
    }

    @Override
    public int cursor() {
        return 0;
    }
}
