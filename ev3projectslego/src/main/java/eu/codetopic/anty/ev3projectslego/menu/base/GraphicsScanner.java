package eu.codetopic.anty.ev3projectslego.menu.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import eu.codetopic.anty.ev3projectslego.menu.BaseMode;
import eu.codetopic.anty.ev3projectslego.utils.Utils;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;
import eu.codetopic.anty.ev3projectslego.utils.menu.Menu;
import eu.codetopic.anty.ev3projectslego.utils.menu.MenuItem;
import eu.codetopic.anty.ev3projectslego.utils.menu.SimpleMenuItem;
import eu.codetopic.anty.ev3projectslego.utils.scan.DistanceRangeFinder;
import lejos.hardware.Button;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;

public final class GraphicsScanner {

    private GraphicsScanner() {
    }

    public static boolean isSupported() {
        if (!Hardware.isSet()) return false;
        Model model = Hardware.get().getModel();
        return model.getRangeScanner() != null && model.getDistanceRangeFinder() != null;
    }

    public static void graphicsAroundScan(@Nullable Canvas canvas, boolean lines) {// TODO: 6.10.16 add way to draw results on phone (maybe create another mode for mobile that reports results using rmi)
        if (!Hardware.isSet()) return;// TODO: 29.9.16 show warning to user about no hardware set
        Model model = Hardware.get().getModel();
        DistanceRangeFinder rangeFinder = model.getDistanceRangeFinder();
        RangeScanner rangeScanner = model.getRangeScanner();
        if (rangeFinder == null || rangeScanner == null)
            return;// TODO: 3.10.16 show warning to user about unsupported hardware

        GraphicsDrawer drawer = canvas == null ? null : canvas.getGraphicsDrawer();
        if (drawer != null) {
            drawer.clear();
            drawer.drawString("Scanning...", drawer.getWidth() / 2, drawer.getHeight() / 2,
                    GraphicsDrawer.HCENTER | GraphicsDrawer.VCENTER);
            canvas.apply();
        }
        RangeReadings results = rangeScanner.getRangeValues();

        if (drawer != null) {
            drawer.clear();
            drawer.drawString("Drawing...", drawer.getWidth() / 2, drawer.getHeight() / 2,
                    GraphicsDrawer.HCENTER | GraphicsDrawer.VCENTER);
            canvas.apply();
        }
        drawScan(canvas, results, rangeFinder.getSensorType().getDistanceMaxValue(), lines);
        Utils.waitWhile(Button.ESCAPE::isUp);// TODO: 6.10.16 add way to exit from mobile app
    }

    public static void drawScan(Canvas canvas, RangeReadings results, double maxRange, boolean lines) {
        GraphicsDrawer drawer = canvas.getGraphicsDrawer();
        int width = drawer.getWidth(), height = drawer.getHeight();
        int xAdd, yAdd;
        final double r;
        if (width < height) {
            r = width / 2d;
            xAdd = 0;
            yAdd = (height - width) / 2;
        } else {
            r = height / 2d;
            xAdd = (width - height) / 2;
            yAdd = 0;
        }

        drawer.clear();
        drawer.setStrokeStyle(GraphicsDrawer.DOTTED);
        drawer.drawRect((int) (r + xAdd - 5), (int) (r + yAdd - 5), 10, 10);
        drawer.setStrokeStyle(GraphicsDrawer.SOLID);

        if (!results.isEmpty()) {
            if (lines) {
                RangeReading firstCapture = results.get(0);
                Point first = firstCapture.getRange() == Float.POSITIVE_INFINITY
                        ? null : getPosition(firstCapture, maxRange, r);
                Point last = null;
                for (int i = 1, size = results.size(); i < size; i++) {
                    if (Thread.currentThread().isInterrupted()) break;
                    RangeReading capture = results.get(i);
                    if (capture.getRange() == Float.POSITIVE_INFINITY) {
                        last = null;
                        continue;
                    }
                    Point pos = getPosition(capture, maxRange, r);
                    if (last != null)
                        drawer.drawLine(last.getX() + xAdd, last.getY() + yAdd, pos.getX() + xAdd, pos.getY() + yAdd);
                    last = pos;
                }
                if (last != null && first != null)
                    drawer.drawLine(last.getX() + xAdd, last.getY() + yAdd, first.getX() + xAdd, first.getY() + yAdd);
            } else {
                for (RangeReading capture : results) {
                    if (Thread.currentThread().isInterrupted()) break;
                    Point pos = getPosition(capture, maxRange, r);
                    drawer.drawRect(pos.getX() + xAdd - 1, pos.getY() + yAdd - 1, 2, 2);
                }
            }
        }
        canvas.apply();
    }

    private static Point getPosition(RangeReading capture, double maxRange, double r) {
        double angle = capture.getAngle();
        double range = capture.getRange();

        return new Point((int) (Math.cos((angle - 90) * Model.DEG_TO_RAD_MUL) * (r * (range / maxRange)) + r),
                (int) (Math.sin((angle - 90) * Model.DEG_TO_RAD_MUL) * (r * (range / maxRange)) + r));
    }

    private static final class Point {

        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static final class GraphicsScannerLines extends BaseMode {

        public static boolean isSupported() {
            return GraphicsScanner.isSupported();
        }

        @Override
        public void run() {
            graphicsAroundScan(getCanvas(), true);
        }
    }

    public static final class GraphicsScannerDots extends BaseMode {

        public static boolean isSupported() {
            return GraphicsScanner.isSupported();
        }

        @Override
        public void run() {
            graphicsAroundScan(getCanvas(), false);
        }
    }

    public static final class GraphicsScanMode implements MenuItem {


        @Override
        public String getName() {
            return "GraphicsScan";
        }

        @Override
        public boolean onSelected(Menu menu, int index) {
            Canvas canvas = menu.generateSubmenuCanvas(index, Menu.calculateMinMenuHeight(5));
            new Menu(canvas, "Select drawing mode:") {
                private Boolean lines = null;

                @Override
                public MenuItem[] createItems() {
                    return new MenuItem[]{
                            new SimpleMenuItem("DrawLines", (itemMenu, itemIndex) -> {
                                lines = true;
                                itemMenu.quit();
                                return true;
                            }),
                            new SimpleMenuItem("DrawDots", (itemMenu, itemIndex) -> {
                                lines = false;
                                itemMenu.quit();
                                return true;
                            })
                    };
                }

                @Override
                protected void onQuit(@NotNull Looper looper) {
                    super.onQuit(looper);
                    if (lines == null) {
                        canvas.removeSelf();
                        return;
                    }

                    BaseMode mode;
                    mode = lines ? new GraphicsScannerLines()
                            : new GraphicsScannerDots();
                    mode.setCanvas(canvas, true);
                    mode.start();
                }
            }.start();
            return true;
        }
    }
}
