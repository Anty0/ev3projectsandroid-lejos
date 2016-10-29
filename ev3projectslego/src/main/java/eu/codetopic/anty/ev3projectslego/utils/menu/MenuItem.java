package eu.codetopic.anty.ev3projectslego.utils.menu;

public interface MenuItem {

    String getName();

    boolean isEnabled();

    boolean onSelected(Menu menu, int itemIndex);
}
