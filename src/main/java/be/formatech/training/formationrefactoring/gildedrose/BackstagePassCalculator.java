package be.formatech.training.formationrefactoring.gildedrose;

import static java.lang.Math.min;

public class BackstagePassCalculator extends ItemCalculator {

    public BackstagePassCalculator(Item item) {
        super(item);
    }

    @Override
    protected void computeQuality() {
        increaseQuality();
    }

    @Override
    protected void handleExpiration() {
        item.quality = 0;
    }

    @Override
    void increaseQuality() {
        if (item.sellIn <= 5) {
            item.quality = min(MAX_QUALITY, item.quality + 3);
        } else if (item.sellIn <= 10) {
            item.quality = min(MAX_QUALITY, item.quality + 2);
        } else {
            item.quality += 1;
        }
    }
}
