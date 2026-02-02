package org.angellock.impl.commands;

public class CommandResponse {
    private String[] commandName;
    private String sender;
    public static final CommandResponse INVALID = new CommandResponse();

    private CommandResponse(){

    }
    public CommandResponse(String[] commandName, String sender) {
        this.commandName = commandName;
        this.sender = sender;
    }

    public String[] getCommandList() {
        return commandName;
    }

    public String getSender() {
        return sender;
    }

    public boolean isInvalid(){
        return (this.sender == null && this.commandName == null);
    }

    public boolean isFromTerminal(){
        return this.sender.equals("<Terminal>");
    }
}
