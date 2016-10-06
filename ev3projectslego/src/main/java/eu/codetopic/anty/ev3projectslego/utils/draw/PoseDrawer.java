package eu.codetopic.anty.ev3projectslego.utils.draw;

import eu.codetopic.anty.ev3projectslego.utils.Utils;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class PoseDrawer extends Thread {

    private final Canvas canvas;
    private final PoseProvider poseProvider;
    private final boolean waitOnNotify;
    private final GraphicsDrawer drawer;
    private final int centerX, centerY;
    private volatile boolean notified = true;

    public PoseDrawer(Canvas canvas, PoseProvider poseProvider, boolean waitOnNotify) {
        this.canvas = canvas;
        this.poseProvider = poseProvider;
        this.waitOnNotify = waitOnNotify;

        this.drawer = this.canvas.getGraphicsDrawer();
        this.centerX = drawer.getWidth() / 2;
        this.centerY = drawer.getHeight() / 2;

        setDaemon(true);
        setPriority(3);
    }

    @Override
    public void run() {
        drawer.clear();

        while (!Thread.interrupted()) {
            if (waitOnNotify) {
                Utils.waitWhile(() -> !notified);
                notified = false;
            }
            drawPose();
            Thread.yield();
        }
    }

    public void drawPose() {
        drawPose(poseProvider.getPose());
    }

    public void drawPose(Pose pose) {
        drawer.setPixel((int) pose.getX() + centerX,
                (int) pose.getY() + centerY, GraphicsDrawer.BLACK);
        canvas.apply();
    }

    public void notifyPoseChanged() {
        notified = true;
    }
}
