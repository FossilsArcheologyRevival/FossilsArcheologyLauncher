package net.ilexiconn.launcher.ui;

import net.ilexiconn.launcher.Launcher;

import javax.swing.*;
import java.awt.*;

public class LauncherFrame extends DraggableFrame {
    public LauncherFrame(Launcher launcher) {
        super(32);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setTitle("Revival Launcher");
        this.setSize(854, 480);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setAutoRequestFocus(true);
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.DARK_GRAY);
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder());
        this.add(header, BorderLayout.PAGE_START);

        JButton closeButton = new JButton();
        closeButton.setIcon(new ImageIcon(LauncherFrame.class.getResource("/close.png")));
        closeButton.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/close_hover.png")));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            LauncherFrame.this.setVisible(false);
            LauncherFrame.this.dispose();
        });
        header.add(closeButton);

        JButton minimizeButton = new JButton();
        minimizeButton.setIcon(new ImageIcon(LauncherFrame.class.getResource("/minimize.png")));
        minimizeButton.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/minimize_hover.png")));
        minimizeButton.setBorder(BorderFactory.createEmptyBorder());
        minimizeButton.setContentAreaFilled(false);
        minimizeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        minimizeButton.setFocusPainted(false);
        minimizeButton.addActionListener(e -> LauncherFrame.this.setExtendedState(JFrame.ICONIFIED));
        header.add(minimizeButton);

        this.add(new LauncherPanel(this, launcher), BorderLayout.CENTER);

        this.setVisible(true);
    }
}
