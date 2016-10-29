package eu.codetopic.anty.ev3projectslego.mode;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.RMIBasicMode;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.menu.Menu;
import eu.codetopic.anty.ev3projectslego.utils.menu.MenuItem;

public abstract class ModeController implements RMIBasicMode {

    private static final String LOG_TAG = "ModeController";

    public Class<?> getRmiInterface() {
        return RMIBasicMode.class;
    }

    public MenuItem getModeMenuItem() {
        return new ModeMenuItem(RMIModesImpl.getInstance()
                .getModeBasicMode(this).getModeName(), this, 70);
    }

    @Override
    public final boolean isRunning() {
        return RMIModesImpl.getInstance().isRunning(this);
    }

    @Override
    public final boolean start() {
        return RMIModesImpl.getInstance().start(this);
    }

    public final boolean start(Canvas canvas, boolean autoRemoveCanvas) {
        return RMIModesImpl.getInstance().start(this, canvas, autoRemoveCanvas);
    }

    protected abstract void onStart(@Nullable Canvas canvas);

    public static class ModeMenuItem implements MenuItem {

        private final String name;
        private final ModeController mode;
        private final int subCanvasHeight;

        public ModeMenuItem(String name, ModeController mode, int subCanvasHeight) {
            this.name = name;
            this.mode = mode;
            this.subCanvasHeight = subCanvasHeight;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isEnabled() {
            return mode.isSupported();
        }

        @Override
        public boolean onSelected(Menu menu, int itemIndex) {
            return isEnabled() && mode.start(subCanvasHeight == -1 ? null
                    : menu.generateSubmenuCanvas(itemIndex, subCanvasHeight), true);
        }
    }
}
