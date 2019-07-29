package be.formatech.training.formationrefactoring.gildedrose;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

class GildedRose {
    Item[] items;
    private List<ItemCalculator> wrappedItems;

    public GildedRose(Item[] items) {
        this.items = items;
        this.wrappedItems = initCalculators(items);
    }

    private List<ItemCalculator> initCalculators(Item[] items) {
        return Arrays.stream(items).map(ItemCalculator::new).collect(Collectors.toList());
    }

    public void updateQuality() {
        wrappedItems.forEach(this::updateQualityPerItem);
    }

    private void updateQualityPerItem(ItemCalculator itemCalculator) {
        computeQuality(itemCalculator.item);
        computeSellIn(itemCalculator.item);
        handleExpiration(itemCalculator.item);
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