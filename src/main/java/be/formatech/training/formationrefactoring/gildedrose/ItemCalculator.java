package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculator {

    public Item item;

    public ItemCalculator(Item item) {
        this.item = item;
    }

    void updateQualityPerItem() {
        computeQuality(item);
        computeSellIn(item);
        handleExpiration(item);
    }

    void handleExpiration(Item item) {
        if (isExpired(item)) {
            if (!isAgedBrie(item)) {
                if (!isBackstagePass(item)) {
                    decreaseQuality(item);
                } else {
                    item.quality = 0;
                }
            } else {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;
                }
            }
        }
    }

    void computeSellIn(Item item) {
        if (!isSulfuras(item)) {
            item.sellIn = item.sellIn - 1;
        }
    }

    void computeQuality(Item item) {
        if (!isAgedBrie(item) && !isBackstagePass(item)) {
            decreaseQuality(item);
        } else {
            increaseQuality(item);
        }
    }

    void increaseQuality(Item item) {
        if (item.quality < 50) {
            item.quality = item.quality + 1;

            if (isBackstagePass(item)) {
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

    void decreaseQuality(Item item) {
        if (item.quality > 0) {
            if (!isSulfuras(item)) {
                item.quality = item.quality - 1;
            }
        }
    }

    boolean isAgedBrie(Item item) {
        return item.name.equals("Aged Brie");
    }

    boolean isBackstagePass(Item item) {
        return item.name.equals("Backstage passes to a TAFKAL80ETC concert");
    }

    boolean isSulfuras(Item item) {
        return item.name.equals("Sulfuras, Hand of Ragnaros");
    }

    boolean isExpired(Item item) {
        return item.sellIn < 0;
    }

}
