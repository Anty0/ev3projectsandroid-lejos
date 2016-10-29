package eu.codetopic.anty.ev3projectslego.utils.menu;

import org.jetbrains.annotations.NotNull;

import eu.codetopic.anty.ev3projectslego.utils.OutOfRangeException;
import eu.codetopic.anty.ev3projectslego.utils.Rectangle2DInt;
import eu.codetopic.anty.ev3projectslego.utils.Utils;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.draw.drawer.GraphicsDrawer;
import eu.codetopic.anty.ev3projectslego.utils.looper.DrawableLoopJob;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;
import lejos.hardware.Button;
import lejos.hardware.lcd.Font;

public abstract class Menu extends DrawableLoopJob {

    protected static final Font TITLE_FONT = Font.getDefaultFont();
    protected static final Font ITEMS_FONT = Font.getSmallFont();

    private final String title;
    private MenuItem[] items = null;
    private int selectedItem = 0;
    private boolean lastPress = false;

    public Menu(Canvas canvas, String title) {
        this(canvas, false, title);
    }

    public Menu(Canvas canvas, boolean canvasAutoClose, String title) {
        super(canvas, canvasAutoClose);
        this.title = title;
    }

    public static int calculateMinMenuHeight(int itemsLen) {
        return (ITEMS_FONT.getHeight() * itemsLen) + TITLE_FONT.getHeight() + 2;
    }

    @Override
    protected void onStart(@NotNull Looper looper) {
        super.onStart(looper);
        reloadItems();
    }

    public abstract MenuItem[] createItems();

    public void reloadItems() {
        selectedItem = 0;
        items = createItems();
        if (calculateMinMenuHeight(this.items.length) > getCanvas().getDrawer().getHeight())
            throw new OutOfRangeException("There is not enough space for your menu (too many lines or small canvas space)");
        invalidate();
    }

    public Canvas generateSubmenuCanvas(int itemIndex, int height) {
        int availableHeight = getCanvas().getDrawer().getHeight();
        if (height + 6 > availableHeight)
            throw new OutOfRangeException("There is not enough space for your submenu (too many lines or small canvas space)");
        int yCenter = calculateMinMenuHeight(itemIndex) - (ITEMS_FONT.getHeight() / 2);
        int y = yCenter - (height / 2);
        if (y + height + 3 > availableHeight) y = availableHeight - height - 3;
        else if (y - 3 < 0) y = 3;
        int x = getCanvas().getDrawer().getWidth() / 4;
        int width = (int) ((getCanvas().getDrawer().getWidth() - x) * 0.9);
        return getCanvas().createRestrictedCanvas(new Rectangle2DInt(x, y, width, height));
    }

    public String getTitle() {
        return title;
    }

    public MenuItem[] getItems() {
        return items;
    }

    public int getSelectedItemIndex() {
        return selectedItem;
    }

    protected void onSelected(MenuItem item, int index) {
        item.onSelected(this, index);
    }

    @Override
    protected boolean onUpdate() {
        if (lastPress) {
            if (Button.readButtons() == 0) lastPress = false;
            return true;
        }

        int buttons = Button.readButtons();
        lastPress = buttons != 0;
        switch (buttons) {
            case Button.ID_UP:
                if (selectedItem > 0) selectedItem--;
                invalidate();
                return true;
            case Button.ID_DOWN:
                if (selectedItem < items.length - 1) selectedItem++;
                invalidate();
                return true;
            case Button.ID_ENTER:
                onSelected(items[selectedItem], selectedItem);
                invalidate();
                return true;
            case Button.ID_ESCAPE:
                Utils.waitWhile(Button.ESCAPE::isDown);
                quit();
                return true;
        }
        return super.onUpdate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        GraphicsDrawer drawer = canvas.getGraphicsDrawer();

        drawer.setFont(TITLE_FONT);
        drawer.drawString(title, 0, 0, 0);

        int lineY = TITLE_FONT.getHeight();
        drawer.drawLine(0, lineY, drawer.getWidth(), lineY++);
        drawer.drawLine(0, lineY, drawer.getWidth(), lineY);

        String pointer = "->";
        int x = ITEMS_FONT.stringWidth(pointer);
        int textHeight = ITEMS_FONT.getHeight();
        drawer.setFont(ITEMS_FONT);
        if (items != null && items.length != 0) {
            for (int i = 0, len = items.length; i < len; i++) {
                MenuItem item = items[i];
                String text = item.getName();
                int y = lineY + (textHeight * (i + 1));
                drawer.drawString(text, x, y, 0);

                if (!item.isEnabled()) {
                    y -= textHeight / 2;
                    drawer.drawLine(x, y, x + ITEMS_FONT.stringWidth(text), y);
                }
            }
            drawer.drawString(pointer, 0, lineY + (textHeight * (selectedItem + 1)), 0);
        } else {
            drawer.drawString("No items", x, lineY + textHeight, 0);
        }
    }

    @Override
    protected void onQuit(@NotNull Looper looper) {
        super.onQuit(looper);
        onDestroy();
    }

    protected void onDestroy() {
        items = null;
        selectedItem = 0;
        lastPress = false;
    }
}
