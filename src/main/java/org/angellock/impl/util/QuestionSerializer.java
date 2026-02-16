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

import org.angellock.impl.managers.QuestionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionSerializer {
    private String question;
    private Map<String, String> answers = new HashMap<>();
    private boolean validQuestion;
    private final String stringQuestion;
    private final QuestionManager questionManager;
    public QuestionSerializer(String stringQuestion, QuestionManager manager) {
        this.stringQuestion = stringQuestion;
        this.questionManager = manager;
    }

    public void build(){
        String[] strings = stringQuestion.split("丨");
        this.validQuestion = (strings.length == 2);

        if (this.validQuestion) {
            this.question = strings[0];
            this.serializeAnswer(strings[1]);
        }
    }

    public boolean isValid(){
        return this.validQuestion;
    }

    private void serializeAnswer(String stringAnswer){
        Pattern answerPattern = Pattern.compile("([A-Z])\\.([\\u4e00-\\u9fa5\\w.]+)"); // [\u4e00-\u9fa5] Chinese character scope
        Matcher answerMatcher = answerPattern.matcher(stringAnswer);

        while (answerMatcher.find()){
            String key = answerMatcher.group(1);
            String value = answerMatcher.group(2);
            this.answers.put(value, key);
        }
    }

    public String getQuestion(){
        return this.question;
    }

    public String getAnswer(){
        for (String key: this.answers.keySet()){
            if (key.contains(this.questionManager.fetchStringAnswer(this.question))){
                return this.answers.get(key);
            }
        }
        return "";
    }
}
