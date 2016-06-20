package net.ilexiconn.launcher.ui;

import net.ilexiconn.launcher.Launcher;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LauncherPanel extends JPanel {
    public static final Color GREEN = new Color(82, 191, 94);

    public int currentProgress;

    public LauncherPanel(final LauncherFrame frame, final Launcher launcher) {
        super(true);
        this.setLayout(null);

        final JTextField username = new JTextField(launcher.config.get("username").getAsString());
        username.setBounds(604, 368, 200, 30);
        username.setBorder(BorderFactory.createEmptyBorder());
        this.add(username);

        final JTextField password = new JPasswordField(launcher.isCached ? "password" : "");
        password.setBounds(604, 408, 200, 30);
        password.setBorder(BorderFactory.createEmptyBorder());
        this.add(password);

        final JButton play = new JButton();
        play.setBounds(814, 368, 30, 70);
        play.setBorder(BorderFactory.createEmptyBorder());
        play.setContentAreaFilled(false);
        play.setIcon(new ImageIcon(LauncherFrame.class.getResource("/play.png")));
        play.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/play_hover.png")));
        play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        play.addActionListener(e -> new Thread() {
            public void run() {
                try {
                    play.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    launcher.config.addProperty("username", username.getText());
                    launcher.saveConfig();
                    launcher.startMinecraft((name, retry, failureMessage) -> {
                        if (retry) {
                            play.setEnabled(true);
                            username.setEnabled(true);
                            password.setEnabled(true);
                            return null;
                        } else {
                            return password.getText();
                        }
                    }, progress -> {
                        LauncherPanel.this.currentProgress = progress;
                        LauncherPanel.this.currentProgress = 0;
                        if (progress == 100) {
                            play.setEnabled(true);
                            username.setEnabled(true);
                            password.setEnabled(true);
                            frame.setVisible(false);
                        }
                    });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start());
        this.add(play);

        password.addActionListener(e -> play.doClick());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(594, 358, 260, 90);

        g.setColor(LauncherPanel.GREEN);
        g.fillRect(594, 348, (int) (this.currentProgress / 100.0F * 260.0F), 10);

        this.repaint();
    }
}
