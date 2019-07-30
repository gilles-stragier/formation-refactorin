package be.formatech.training.formationrefactoring.gildedrose;

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
        if (item.quality < 50) {
            item.quality = item.quality + 1;

            if (item.sellIn < 11) {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;
                }
            }

            if (item.sellIn < 6) {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;
                }
            }

        }
    }
}
