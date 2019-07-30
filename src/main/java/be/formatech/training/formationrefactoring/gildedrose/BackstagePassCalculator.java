package be.formatech.training.formationrefactoring.gildedrose;

import static java.lang.Math.min;

public class BackstagePassCalculator extends ItemCalculator {

    public BackstagePassCalculator(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        item.sellIn -= 1;

        if (isExpired()) {
            item.quality = 0;
        } else if (item.sellIn < 5) {
            item.quality = min(MAX_QUALITY, item.quality + 3);
        } else if (item.sellIn < 10) {
            item.quality = min(MAX_QUALITY, item.quality + 2);
        } else {
            item.quality += 1;
        }
    }

}
