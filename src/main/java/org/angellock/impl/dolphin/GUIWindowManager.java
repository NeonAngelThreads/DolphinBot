package org.angellock.impl.dolphin;

import org.angellock.impl.RobotPlayer;
import org.angellock.impl.Start;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.win32terminal.AnsiEscapes;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUIWindowManager {
    private JFrame mainWindow;
    private final BotManager botManager;
    private final DolphinWindow window = new DolphinWindow();
    public GUIWindowManager(BotManager botManager) {
        this.botManager = botManager;

        this.mainWindow = new JFrame("DolphinBot " + Start.getArchiveVersion());
    }

    // manager.getBots()
    public void startGUI(){
        System.out.println(botManager.getBots().toString());
        window.getTabbedPane1().setVisible(true);
    }
}
