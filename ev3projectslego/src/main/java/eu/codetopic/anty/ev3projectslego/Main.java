package eu.codetopic.anty.ev3projectslego;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.net.Server;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import eu.codetopic.anty.ev3projectsbase.BaseConstants;
import eu.codetopic.anty.ev3projectsbase.BaseConstants.SingleClientDetector;
import eu.codetopic.anty.ev3projectsbase.DefaultModelInfo;
import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.anty.ev3projectsbase.RMIModes.BasicMode;
import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.RMIHardwareImpl;
import eu.codetopic.anty.ev3projectslego.hardware.model.ModelImpl;
import eu.codetopic.anty.ev3projectslego.mode.RMIModesImpl;
import eu.codetopic.anty.ev3projectslego.utils.Utils;
import eu.codetopic.anty.ev3projectslego.utils.draw.ButtonDrawer;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import eu.codetopic.anty.ev3projectslego.utils.looper.DrawableLoopJob;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;
import eu.codetopic.anty.ev3projectslego.utils.menu.Menu;
import eu.codetopic.anty.ev3projectslego.utils.menu.MenuItem;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.internal.ev3.EV3LED;

public class Main {

    private static boolean restoreMyModel() {
        ModelInfo myModelInfo = Hardware.loadMyModelInfo();
        if (myModelInfo != null) {
            try {
                Hardware.setup(new ModelImpl(myModelInfo));
                return true;
            } catch (Throwable t) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException, LipeRMIException {
        Hardware.LED.setPattern(EV3LED.COLOR_ORANGE, EV3LED.PATTERN_HEARTBEAT);
        {
            GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();
            lcd.drawString("Loading...", lcd.getWidth() / 2, lcd.getHeight() / 2,
                    GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
            lcd.refresh();
        }
        Looper.prepare();

        boolean success = restoreMyModel();

        RMIHardwareImpl rmiHardware = new RMIHardwareImpl();
        rmiHardware.start();
        RMIModesImpl.initialize(BaseConstants.CALL_HANDLER, Looper.myLooper());
        BaseConstants.initForServer(rmiHardware, RMIModesImpl.getInstance());
        Server server = new Server();
        SingleClientDetector clientDetector = BaseConstants.startSingleClientServer(server);

        if (!Hardware.isSet()) {
            Hardware.LED.setPattern(EV3LED.COLOR_RED, EV3LED.PATTERN_ON);
            new DrawableLoopJob(Canvas.obtain(false), true) {
                boolean settingUpModel = false;
                boolean exception = false;

                @Override
                protected void onStart(@NotNull Looper looper) {
                    getCanvas().getGraphicsDrawer().setFont(Font.getSmallFont());
                    super.onStart(looper);
                }

                @Override
                protected boolean onUpdate() {
                    int buttons = Button.readButtons();
                    if (!exception && buttons == Button.ID_ENTER) {
                        Utils.waitWhile(Button.ENTER::isDown);
                        Hardware.LED.setPattern(EV3LED.COLOR_ORANGE, EV3LED.PATTERN_HEARTBEAT);
                        settingUpModel = true;
                        draw();
                        try {
                            Hardware.setup(new ModelImpl(DefaultModelInfo.getInstance()));
                        } catch (Throwable t) {
                            exception = true;
                            settingUpModel = false;
                            Hardware.LED.setPattern(EV3LED.COLOR_RED, EV3LED.PATTERN_ON);
                            draw();
                        }
                        return true;
                    }
                    if (buttons == Button.ID_ESCAPE || Hardware.isSet()) {
                        Utils.waitWhile(Button.ESCAPE::isDown);
                        quit();
                        return true;
                    }
                    return super.onUpdate();
                }

                @Override
                protected void onDraw(Canvas canvas) {
                    GraphicsDrawer drawer = canvas.getGraphicsDrawer();
                    if (settingUpModel) {
                        drawer.drawString("Preparing model...",
                                drawer.getWidth() / 2, drawer.getHeight() / 2,
                                GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
                        return;
                    }

                    drawer.drawString(success || exception ? "No model set" : "Can't use your model",
                            drawer.getWidth() / 2, drawer.getHeight() / 2 - drawer.getFont().getHeight(),
                            GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
                    drawer.drawString("Setup your model",
                            drawer.getWidth() / 2, drawer.getHeight() / 2 + drawer.getFont().getHeight(),
                            GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
                    drawer.drawString("from mobile app",
                            drawer.getWidth() / 2, drawer.getHeight() / 2 + 2 * drawer.getFont().getHeight(),
                            GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
                    if (exception) {
                        drawer.drawString("Can't use Default model for your vehicle",
                                drawer.getWidth() / 2, drawer.getHeight() / 2 + drawer.getFont().getHeight(),
                                GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
                    } else {
                        ButtonDrawer.drawCenterButton(drawer, "Default model");
                    }
                    ButtonDrawer.drawLeftButton(drawer, "Exit");
                }

                @Override
                protected void onQuit(@NotNull Looper looper) {
                    super.onQuit(looper);
                    looper.quit();
                }
            }.start();
            Looper.loop();
        }

        if (Hardware.isSet()) {
            Hardware.LED.setPattern(EV3LED.COLOR_ORANGE, EV3LED.PATTERN_ON);
            new Menu(Canvas.obtain(true), true, "EV3Projects") {
                boolean lastClientState = clientDetector.isClientConnected();

                @Override
                public MenuItem[] createItems() {
                    RMIModesImpl rmiModes = RMIModesImpl.getInstance();
                    return new MenuItem[]{
                            rmiModes.getModeController(BasicMode.GRAPHICS_SCAN_LINES).getModeMenuItem(),
                            rmiModes.getModeController(BasicMode.GRAPHICS_SCAN_DOTS).getModeMenuItem(),
                            rmiModes.getModeController(BasicMode.BEACON_FOLLOW).getModeMenuItem()};
                }

                @Override
                protected boolean onUpdate() {
                    boolean clientState = clientDetector.isClientConnected();
                    if (clientState != lastClientState) {
                        invalidate();
                        lastClientState = clientState;
                    }
                    return !clientState && super.onUpdate();
                }

                @Override
                protected void onDraw(Canvas canvas) {
                    if (clientDetector.isClientConnected()) {
                        GraphicsDrawer drawer = canvas.getGraphicsDrawer();
                        drawer.setFont(Font.getDefaultFont());
                        drawer.drawString("Remote controlled", drawer.getWidth() / 2,
                                drawer.getHeight() / 2, GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
                        // TODO: 14.10.16 show info about running mode
                        return;
                    }
                    super.onDraw(canvas);
                }

                @Override
                protected void onQuit(@NotNull Looper looper) {
                    super.onQuit(looper);
                    looper.quit();
                }
            }.start();
            Looper.loop();
        }

        Hardware.LED.setPattern(EV3LED.COLOR_ORANGE, EV3LED.PATTERN_HEARTBEAT);

        Looper.myLooper().destroy();
        server.close();

        System.exit(0);// FIXME: 14.10.16 try find blocking thread
    }
}
