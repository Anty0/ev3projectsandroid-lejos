package eu.codetopic.anty.ev3projectslego;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import eu.codetopic.anty.ev3projectsbase.BaseConstants;
import eu.codetopic.anty.ev3projectsbase.DefaultModelInfo;
import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.RMIHardwareImpl;
import eu.codetopic.anty.ev3projectslego.hardware.model.ModelImpl;
import eu.codetopic.anty.ev3projectslego.menu.RMIModesImpl;
import eu.codetopic.anty.ev3projectslego.menu.base.BeaconFollow;
import eu.codetopic.anty.ev3projectslego.menu.base.GraphicsScanner;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;
import eu.codetopic.anty.ev3projectslego.utils.menu.Menu;
import eu.codetopic.anty.ev3projectslego.utils.menu.MenuItem;
import eu.codetopic.anty.ev3projectslego.utils.menu.SimpleMenuItem;
import lipermi.exception.LipeRMIException;
import lipermi.net.Server;

public class Main {

    public static Menu MAIN_MENU;

    public static void main(String[] args) throws IOException, LipeRMIException {
        Looper.prepare();

        MAIN_MENU = new Menu(Canvas.obtain(true), "EV3Project") {
            @Override
            public MenuItem[] createItems() {
                ArrayList<MenuItem> items = new ArrayList<>();
                if (GraphicsScanner.isSupported())
                    items.add(new GraphicsScanner.GraphicsScanMode());
                if (BeaconFollow.isSupported()) items.add(new BeaconFollow.BeaconFollowMode());

                if (!Hardware.isSet() || items.isEmpty()) {
                    items.clear();
                    items.add(new SimpleMenuItem("No modes available for you.", null));
                    items.add(new SimpleMenuItem("Please setup your model from mobile app or", null));
                    items.add(new SimpleMenuItem("Use > Default model", (menu, itemIndex) -> {
                        Hardware.setup(new ModelImpl(DefaultModelInfo.getInstance()));
                        return true;
                    }));
                    // TODO: 6.10.16 show uploaded models from mobile app
                }

                return items.toArray(new MenuItem[items.size()]);
            }

            @Override
            protected void onQuit(@NotNull Looper looper) {
                super.onQuit(looper);
                looper.quit();
            }
        };

        RMIHardwareImpl rmiHardware = new RMIHardwareImpl();
        rmiHardware.start();
        RMIModesImpl.initialize(Looper.myLooper());
        Server server = BaseConstants.startServer(rmiHardware,
                RMIModesImpl.getInstance());

        MAIN_MENU.start();

        Looper.loop();

        server.close();
    }
}
