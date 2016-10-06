package eu.codetopic.anty.ev3projectslego.utils.menu;

import org.jetbrains.annotations.Nullable;

public class SimpleMenuItem implements MenuItem {

    private final String name;
    private final OnSelected onSelected;

    public SimpleMenuItem(String name, @Nullable OnSelected onSelected) {
        this.name = name;
        this.onSelected = onSelected;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean onSelected(Menu menu, int itemIndex) {
        return onSelected != null && onSelected.onSelected(menu, itemIndex);
    }

    public interface OnSelected {
        boolean onSelected(Menu menu, int itemIndex);
    }
}
