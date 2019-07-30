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
        if (isExpired() && item.quality < MAX_QUALITY) {
            item.quality = item.quality + 1;
        }

    }
}
