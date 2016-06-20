package net.ilexiconn.launcher.ui;

import com.rometools.rome.feed.synd.SyndEntry;
import net.ilexiconn.launcher.Launcher;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LauncherPanel extends JPanel {
    public static final Color GREEN = new Color(82, 191, 94);
    public static final Color RED = new Color(255, 84, 84);

    public int currentProgress;
    public int taskCount = -1;
    public int currentTask;

    public Launcher launcher;
    public Font font;
    public BufferedImage banner;

    public LauncherPanel(final LauncherFrame frame, final Launcher launcher) {
        super(true);
        this.launcher = launcher;
        this.setLayout(null);

        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, LauncherPanel.class.getResourceAsStream("/roboto_thin.ttf"));
            this.font = this.font.deriveFont(Font.PLAIN, 32);
            this.banner = ImageIO.read(LauncherPanel.class.getResourceAsStream("/banner.png"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        final JTextField username = new JTextField(launcher.config.get("username").getAsString());
        username.setBounds(604, 368, 200, 30);
        username.setBorder(BorderFactory.createEmptyBorder());
        username.setFont(this.font.deriveFont(Font.PLAIN, 20));
        this.add(username);

        final JTextField password = new JPasswordField(launcher.isCached ? "password" : "");
        password.setBounds(604, 408, 200, 30);
        password.setBorder(BorderFactory.createEmptyBorder());
        password.setFont(this.font.deriveFont(Font.PLAIN, 20));
        ((JPasswordField) password).setEchoChar('*');
        this.add(password);

        final JButton play = new JButton();
        play.setBounds(814, 368, 30, 70);
        play.setBorder(BorderFactory.createEmptyBorder());
        play.setContentAreaFilled(false);
        play.setIcon(new ImageIcon(LauncherFrame.class.getResource("/play.png")));
        play.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/play_hover.png")));
        play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        play.setFocusPainted(false);
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

        new Thread() {
            public void run() {
                while (LauncherPanel.this.launcher.feed == null) {
                    System.out.print("");
                }

                SyndEntry entry = LauncherPanel.this.launcher.feed.getEntries().get(0);
                JButton button = new JButton();
                button.setBounds(570, 413, 19, 19);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setIcon(new ImageIcon(LauncherFrame.class.getResource("/more.png")));
                button.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/more_hover.png")));
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setFocusPainted(false);
                button.addActionListener(e -> {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(entry.getUri()));
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                LauncherPanel.this.add(button);
            }
        }.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g.drawImage(this.banner, 0, 0, Math.max(854, this.banner.getWidth()), Math.max(358, this.banner.getHeight()), null);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 358, 854, 90);

        if (this.taskCount >= 0) {
            g.setFont(this.font.deriveFont(Font.PLAIN, 20));
            String text = (int) (this.currentTask + 1.0F) + "/" + (int) (this.taskCount + 1.0F);
            int width = g.getFontMetrics().stringWidth(text);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 312, 854, 36);

            g.setColor(Color.WHITE);
            g.drawString(text, 427 - width / 2, 338);

            g.setColor(LauncherPanel.RED);
            g.fillRect(0, 348, 854, 10);

            g.setColor(LauncherPanel.GREEN);
            g.fillRect(0, 348, (int) (this.currentProgress / 100.0F * 854.0F), 10);
        }

        if (this.launcher.feed != null) {
            SyndEntry entry = this.launcher.feed.getEntries().get(0);
            g.setColor(Color.WHITE);
            g.setFont(this.font);
            g.drawString(entry.getTitle(), 10, 398);
            g.setFont(this.font.deriveFont(Font.PLAIN, 20));
            g.drawString(Jsoup.parse(entry.getContents().get(0).getValue()).text().split("   ")[0], 10, 430);
        }

        this.repaint();
    }

}
