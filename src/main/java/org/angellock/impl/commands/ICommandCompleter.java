package org.angellock.impl.commands;

import java.util.List;

public interface ICommandCompleter {
    List<String> complete(String[] commandList);
}
