package net.ilexiconn.launcher.ui;

import net.ilexiconn.launcher.Launcher;
import net.ilexiconn.launcher.resource.ResourceLocation;

import javax.swing.*;
import java.awt.*;

public class LauncherFrame extends DraggableFrame {
    public static final ResourceLocation LOGO = new ResourceLocation("textures/logo.png");
    public static final ResourceLocation CLOSE = new ResourceLocation("textures/close.png");
    public static final ResourceLocation CLOSE_HOVER = new ResourceLocation("textures/close_hover.png");
    public static final ResourceLocation MINIMIZE = new ResourceLocation("textures/minimize.png");
    public static final ResourceLocation MINIMIZE_HOVER = new ResourceLocation("textures/minimize_hover.png");

    public LauncherPanel panel;

    public LauncherFrame() {
        super(32);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setTitle("Revival Launcher");
        this.setIconImage(Launcher.INSTANCE.resourceLoader.loadImage(LauncherFrame.LOGO));
        this.setSize(854, 480);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setAutoRequestFocus(true);
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.DARK_GRAY);
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder());
        this.add(header, BorderLayout.PAGE_START);

        JButton closeButton = new JButton();
        closeButton.setIcon(Launcher.INSTANCE.resourceLoader.loadIcon(LauncherFrame.CLOSE));
        closeButton.setRolloverIcon(Launcher.INSTANCE.resourceLoader.loadIcon(LauncherFrame.CLOSE_HOVER));
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
        minimizeButton.setIcon(Launcher.INSTANCE.resourceLoader.loadIcon(LauncherFrame.MINIMIZE));
        minimizeButton.setRolloverIcon(Launcher.INSTANCE.resourceLoader.loadIcon(LauncherFrame.MINIMIZE_HOVER));
        minimizeButton.setBorder(BorderFactory.createEmptyBorder());
        minimizeButton.setContentAreaFilled(false);
        minimizeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        minimizeButton.setFocusPainted(false);
        minimizeButton.addActionListener(e -> LauncherFrame.this.setExtendedState(JFrame.ICONIFIED));
        header.add(minimizeButton);

        this.add(this.panel = new LauncherPanel(), BorderLayout.CENTER);

        this.setVisible(true);
    }
}
