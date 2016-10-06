package eu.codetopic.anty.ev3projectslego.utils.menu;

public interface MenuItem {

    String getName();

    boolean onSelected(Menu menu, int itemIndex);
}
