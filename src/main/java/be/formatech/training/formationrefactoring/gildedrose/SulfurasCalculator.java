package be.formatech.training.formationrefactoring.gildedrose;

public class SulfurasCalculator extends ItemCalculator {

    public SulfurasCalculator(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        computeQuality();
        computeSellIn();
        handleExpiration();
    }

    @Override
    protected void computeSellIn() {}

    @Override
    protected void decreaseQuality() {}
}
