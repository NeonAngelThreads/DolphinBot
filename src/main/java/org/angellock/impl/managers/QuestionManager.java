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

package org.angellock.impl.managers;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class QuestionManager extends ResourceHelper{
    private Map<String, JsonElement> questionMap;

    public QuestionManager(@Nullable String defaultPath, String fileType) {
        super(defaultPath, fileType);
    }

    public QuestionManager(String filename){
        super(null, filename);
    }

    @Override
    public String getFileName() {
        return "server.queue.questions";
    }

    public String fetchStringAnswer(String question){
        JsonElement element = this.questionMap.get(question);
        if (element == null){
            return "";
        }
        return element.getAsString();
    }

    public QuestionManager load(){
        if (this.questionMap == null) {
            this.questionMap = readJSONContent();
        }
        return this;
    }
}
