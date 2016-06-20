package net.ilexiconn.launcher;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Progressbar {
    public void display(final String string, final File target, final IProgressbarCallback callback) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setSize(300, 70);
        final JFrame frame = new JFrame();
        frame.setContentPane(progressBar);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
        frame.setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Thread() {
            public void run() {
                try {
                    URL url = new URL(string);
                    HttpURLConnection connection = (HttpURLConnection) (url.openConnection());
                    long contentLength = connection.getContentLength();
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    OutputStream outputStream = new FileOutputStream(target);
                    OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 1024);
                    byte[] data = new byte[1024];
                    long downloaded = 0;
                    int i;
                    while ((i = inputStream.read(data, 0, 1024)) >= 0) {
                        downloaded += i;

                        final int currentProgress = (int) ((((double) downloaded) / ((double) contentLength)) * 100.0D);
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(currentProgress);
                            progressBar.setString(currentProgress + "%");
                        });

                        bufferedOutputStream.write(data, 0, i);
                    }
                    bufferedOutputStream.close();
                    inputStream.close();
                    frame.setVisible(false);
                    frame.dispose();
                    callback.call();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public interface IProgressbarCallback {
        void call();
    }
}
