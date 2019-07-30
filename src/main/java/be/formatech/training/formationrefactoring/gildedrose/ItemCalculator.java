package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculator {

    public static final int MAX_QUALITY = 50;

    public Item item;

    public ItemCalculator(Item item) {
        this.item = item;
    }

    public void updateQuality() {
        item.sellIn -= 1;

        if (isExpired()) {
            item.quality = Math.max(0, item.quality - 2);
        } else {
            item.quality = Math.max(0, item.quality - 1);
        }
    }

    boolean isExpired() {
        return item.sellIn < 0;
    }

}
