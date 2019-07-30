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

    void computeQuality() {
        if (!isAgedBrie() && !isBackstagePass()) {
            decreaseQuality();
        } else {
            increaseQuality();
        }
    }

    void increaseQuality() {
        if (item.quality < 50) {
            item.quality = item.quality + 1;

            if (isBackstagePass()) {
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

    void decreaseQuality() {
        if (item.quality > 0) {
            if (!isSulfuras()) {
                item.quality = item.quality - 1;
            }
        }
    }

    boolean isAgedBrie() {
        return item.name.equals("Aged Brie");
    }

    boolean isBackstagePass() {
        return item.name.equals("Backstage passes to a TAFKAL80ETC concert");
    }

    boolean isSulfuras() {
        return item.name.equals("Sulfuras, Hand of Ragnaros");
    }

    boolean isExpired() {
        return item.sellIn < 0;
    }

}
