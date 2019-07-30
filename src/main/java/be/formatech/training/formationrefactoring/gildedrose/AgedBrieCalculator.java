package be.formatech.training.formationrefactoring.gildedrose;

public class AgedBrieCalculator extends ItemCalculator {

    public AgedBrieCalculator(Item item) {
        super(item);
    }

    @Override
    protected void computeQuality() {
        increaseQuality();
    }

    @Override
    protected void handleExpiration() {
        if (item.quality < MAX_QUALITY) {
            item.quality = item.quality + 1;
        }
    }
}
