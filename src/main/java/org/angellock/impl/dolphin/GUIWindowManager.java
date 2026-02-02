package org.angellock.impl.dolphin;

import org.angellock.impl.Start;
import org.angellock.impl.managers.BotManager;

import javax.swing.*;

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
        System.out.println(botManager.bots().toString());
        window.initWindow();
    }
}
