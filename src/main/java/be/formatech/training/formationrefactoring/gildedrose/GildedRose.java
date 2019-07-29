package be.formatech.training.formationrefactoring.gildedrose;

import java.util.Arrays;
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
        wrappedItems.forEach(ItemCalculator::updateQuality);
    }
}