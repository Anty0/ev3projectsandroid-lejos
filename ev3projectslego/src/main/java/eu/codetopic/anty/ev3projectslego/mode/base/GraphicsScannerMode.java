package eu.codetopic.anty.ev3projectslego.mode.base;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResult;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;
import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import eu.codetopic.anty.ev3projectslego.mode.ModeController;
import eu.codetopic.anty.ev3projectslego.utils.Utils;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.RangeScanner;
import lejos.hardware.Button;

public final class GraphicsScannerMode {// TODO: 15.10.16 add mode to getting results from phone

    private GraphicsScannerMode() {
    }

    private static boolean isSupported() {
        if (!Hardware.isSet()) return false;
        Model model = Hardware.get().getModel();
        return model.getRangeScanner() != null;
    }

    private static void graphicsAroundScan(@Nullable Canvas canvas, boolean lines) {
        if (!Hardware.isSet()) return;
        Model model = Hardware.get().getModel();
        RangeScanner rangeScanner = model.getRangeScanner();
        if (rangeScanner == null) return;

        GraphicsDrawer drawer = canvas == null ? null : canvas.getGraphicsDrawer();
        if (drawer != null) {
            drawer.clear();
            drawer.drawString("Scanning...", drawer.getWidth() / 2, drawer.getHeight() / 2,
                    GraphicsDrawer.HCENTER | GraphicsDrawer.VCENTER);
            canvas.apply();
        }
        ScanResults results = rangeScanner.aroundScan(RangeScanner.MOTOR_SCAN_SPEED_SLOW);

        if (drawer != null) {
            drawer.clear();
            drawer.drawString("Drawing...", drawer.getWidth() / 2, drawer.getHeight() / 2,
                    GraphicsDrawer.HCENTER | GraphicsDrawer.VCENTER);
            canvas.apply();
        }
        drawScan(canvas, results, rangeScanner.getMaxDistance(), lines);
        Utils.waitWhile(Button.ESCAPE::isUp);
    }

    private static void drawScan(Canvas canvas, ScanResults results, double maxDistance, boolean lines) {
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
                ScanResult firstCapture = results.get(0);
                Point first = firstCapture.getDistance() == Float.POSITIVE_INFINITY
                        ? null : getPosition(firstCapture, maxDistance, r);
                Point last = null;
                for (int i = 1, size = results.size(); i < size; i++) {
                    if (Thread.currentThread().isInterrupted()) break;
                    ScanResult capture = results.get(i);
                    if (capture.getDistance() == Float.POSITIVE_INFINITY) {
                        last = null;
                        continue;
                    }
                    Point pos = getPosition(capture, maxDistance, r);
                    if (last != null)
                        drawer.drawLine(last.getX() + xAdd, last.getY() + yAdd, pos.getX() + xAdd, pos.getY() + yAdd);
                    last = pos;
                }
                if (last != null && first != null)
                    drawer.drawLine(last.getX() + xAdd, last.getY() + yAdd, first.getX() + xAdd, first.getY() + yAdd);
            } else {
                for (ScanResult capture : results) {
                    if (Thread.currentThread().isInterrupted()) break;
                    Point pos = getPosition(capture, maxDistance, r);
                    drawer.drawRect(pos.getX() + xAdd - 1, pos.getY() + yAdd - 1, 2, 2);
                }
            }
        }
        canvas.apply();
    }

    private static Point getPosition(ScanResult capture, double maxRange, double r) {
        double angle = capture.getAngle();
        double distance = capture.getDistance();

        return new Point((int) (Math.cos(Math.toRadians(angle - 90)) * (r * (distance / maxRange)) + r),
                (int) (Math.sin(Math.toRadians(angle - 90)) * (r * (distance / maxRange)) + r));
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

    public static final class Lines extends ModeController {

        @Override
        public boolean isSupported() {
            return GraphicsScannerMode.isSupported();
        }

        @Override
        protected void onStart(@Nullable Canvas canvas) {
            graphicsAroundScan(canvas, true);
        }
    }

    public static final class Dots extends ModeController {

        @Override
        public boolean isSupported() {
            return GraphicsScannerMode.isSupported();
        }

        @Override
        protected void onStart(@Nullable Canvas canvas) {
            graphicsAroundScan(canvas, false);
        }
    }

    /*public static final class GraphicsScanMenuItem implements MenuItem {


        @Override
        public String getName() {
            return "GraphicsScan";
        }

        @Override
        public boolean isEnabled() {
            return isSupported();
        }

        @Override
        public boolean onSelected(Menu menu, int index) {
            if (!isEnabled()) return false;
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

                    ModeRunner mode;
                    mode = lines ? new Lines()
                            : new Dots();
                    mode.setCanvas(canvas, true);
                    mode.start();
                }
            }.start();
            return true;
        }
    }*/
}
