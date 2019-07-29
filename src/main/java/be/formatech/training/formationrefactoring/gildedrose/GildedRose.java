package be.formatech.training.formationrefactoring.gildedrose;

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = 0; i < items.length; i++) {

            if (!isAgedBrie(items[i]) && !isBackstagePass(items[i])) {
                decreaseQuality(items[i]);
            } else {
                increaseQuality(items[i]);
            }

            if (!isSulfuras(items[i])) {
                items[i].sellIn = items[i].sellIn - 1;
            }

            if (isExpired(items[i])) {
                if (!isAgedBrie(items[i])) {
                    if (!isBackstagePass(items[i])) {
                        decreaseQuality(items[i]);
                    } else {
                        items[i].quality = items[i].quality - items[i].quality;
                    }
                } else {
                    if (items[i].quality < 50) {
                        items[i].quality = items[i].quality + 1;
                    }
                }
            }
        }
    }

    private void increaseQuality(Item item) {
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

    private void decreaseQuality(Item item) {
        if (item.quality > 0) {
            if (!isSulfuras(item)) {
                item.quality = item.quality - 1;
            }
        }
    }

    private boolean isAgedBrie(Item item) {
        return item.name.equals("Aged Brie");
    }

    private boolean isBackstagePass(Item item) {
        return item.name.equals("Backstage passes to a TAFKAL80ETC concert");
    }

    private boolean isSulfuras(Item item) {
        return item.name.equals("Sulfuras, Hand of Ragnaros");
    }

    private boolean isExpired(Item item) {
        return item.sellIn < 0;
    }


}