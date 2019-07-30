package be.formatech.training.formationrefactoring.gildedrose;

public class AgedBrieCalculator extends ItemCalculator {

    public AgedBrieCalculator(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        item.sellIn -= 1;

        if (isExpired()) {
            item.quality = Math.min(MAX_QUALITY, item.quality + 2);
        } else {
            item.quality = Math.min(MAX_QUALITY, item.quality + 1);
        }
    }

}
