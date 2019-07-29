package be.formatech.training.formationrefactoring.gildedrose;

public class AgedBrieCalculator extends ItemCalculator {

    public AgedBrieCalculator(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        decreaseSellIn();

        if (isExpired()) {
            increaseQualityBy(2);
        } else {
            increaseQualityBy(1);
        }
    }

}
