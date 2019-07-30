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
}
