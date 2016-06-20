package net.ilexiconn.launcher.ui;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class DraggableFrame extends JFrame implements MouseListener, MouseMotionListener {
    private int offsetX;
    private int offsetY;
    private int headerHeight;
    private boolean dragging;

    public DraggableFrame(int headerHeight) {
        this.setUndecorated(true);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.headerHeight = headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getY() <= this.headerHeight) {
            this.dragging = true;
            this.offsetX = e.getX();
            this.offsetY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.dragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.dragging) {
            this.setLocation(e.getXOnScreen() - this.offsetX, e.getYOnScreen() - this.offsetY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
