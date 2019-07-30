package be.formatech.training.formationrefactoring.gildedrose;

public class BackstagePassCalculator extends ItemCalculator {

    public BackstagePassCalculator(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        decreaseSellIn();

        if (isExpired()) {
            item.quality = 0;
        } else if (item.sellIn < 5) {
            increaseQualityBy(3);
        } else if (item.sellIn < 10) {
            increaseQualityBy(2);
        } else {
            increaseQualityBy(1);
        }
    }

}
