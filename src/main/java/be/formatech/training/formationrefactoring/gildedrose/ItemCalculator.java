package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculator {

    public static final int MAX_QUALITY = 50;

    public Item item;

    public ItemCalculator(Item item) {
        this.item = item;
    }

    public void updateQuality() {
        item.sellIn -= 1;

        if (isExpired()) {
            item.quality = Math.max(0, item.quality - 2);
        } else {
            item.quality = Math.max(0, item.quality - 1);
        }
    }

    protected void handleExpiration() {
        if (isExpired()) {
            decreaseQuality();
        }
    }

    protected void computeSellIn() {
        item.sellIn -= 1;
    }

    protected void computeQuality() {
        decreaseQuality();
    }

    protected void increaseQuality() {
        item.quality = Math.min(MAX_QUALITY, item.quality + 1);
    }

    protected void decreaseQuality() {
        item.quality = Math.max(0, item.quality - 1);
    }

    boolean isExpired() {
        return item.sellIn < 0;
    }

}
