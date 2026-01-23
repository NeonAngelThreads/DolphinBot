package org.angellock.impl.dolphin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DolphinWindow {
    private JPanel mainWindow;
    private JTabbedPane tabbedPane1;
    private JButton stopButton;
    private JButton startButton;
    private JButton restartButton;
    private JTable table1;
    private JButton open_folder;
    private JButton add_bot = new JButton();
    private JEditorPane hFdFsdFsEditorPane;
    private JButton settings;
    private JButton start_all;
    private JButton stop_all;
    private JButton restart_all;

    private JFrame frame;

    public DolphinWindow(JFrame frame) {
        this.frame = frame;
    }

    public void initWindow() {
        this.frame.setSize(800, 600);
        this.frame.setContentPane(mainWindow);
        this.frame.setVisible(true);
    }
    public DolphinWindow(JPanel mainWindow) {
        this.mainWindow = mainWindow;
        add_bot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("action test");
            }
        });
    }

    public JTabbedPane getTabbedPane1() {
        return tabbedPane1;
    }
}
