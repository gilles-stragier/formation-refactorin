package be.formatech.training.formationrefactoring.gildedrose;

import static java.lang.Math.min;

public class ItemCalculator {

    public static final int MAX_QUALITY = 50;

    public Item item;

    public ItemCalculator(Item item) {
        this.item = item;
    }

    protected void decreaseSellIn() {
        item.sellIn -= 1;
    }

    public void updateQuality() {
        decreaseSellIn();

        if (isExpired()) {
            item.quality = Math.max(0, item.quality - 2);
        } else {
            item.quality = Math.max(0, item.quality - 1);
        }
    }

    boolean isExpired() {
        return item.sellIn < 0;
    }

    protected void increaseQualityBy(int n) {
        item.quality = min(MAX_QUALITY, item.quality + n);
    }
}
