package org.angellock.impl.dolphin;

import org.angellock.impl.RobotPlayer;
import org.angellock.impl.Start;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.win32terminal.AnsiEscapes;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUIWindowManager {
    private final BotManager botManager;
    private DolphinWindow window;
    public GUIWindowManager(BotManager botManager) {
        this.botManager = botManager;
        JFrame frame = new JFrame("DolphinBot " + Start.getArchiveVersion());
        this.window = new DolphinWindow(frame);
    }

    // manager.getBots()
    public void startGUI(){
        System.out.println(botManager.getBots().toString());
        window.initWindow();
    }
}
