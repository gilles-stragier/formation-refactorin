package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculator {

    public Item item;

    public ItemCalculator(Item item) {
        this.item = item;
    }

    public void updateQuality() {
        computeQuality();
        computeSellIn();
        if (isExpired()) {
            handleExpiration();
        }
    }

    protected void handleExpiration() {
        decreaseQuality();
    }

    protected void computeSellIn() {
        item.sellIn = item.sellIn - 1;
    }

    protected void computeQuality() {
        decreaseQuality();
    }

    void increaseQuality() {
        if (item.quality < 50) {
            item.quality = item.quality + 1;
        }
    }

    protected void decreaseQuality() {
        if (item.quality > 0) {
            item.quality = item.quality - 1;
        }
    }

    boolean isExpired() {
        return item.sellIn < 0;
    }

}
