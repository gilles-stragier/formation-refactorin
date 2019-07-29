package be.formatech.training.formationrefactoring.gildedrose;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class GildedRose {
    Item[] items;
    private final List<ItemCalculator> wrappedItems;
    private final ItemCalculatorFactory itemCalculatorFactory;

    public GildedRose(Item[] items, ItemCalculatorFactory itemCalculatorFactory) {
        this.items = items;
        this.itemCalculatorFactory = itemCalculatorFactory;
        this.wrappedItems = initCalculators(items);
    }

    public GildedRose(Item[] items) {
        this(items, new ItemCalculatorFactory());
    }

    private List<ItemCalculator> initCalculators(Item[] items) {
        return Arrays.stream(items).map(itemCalculatorFactory::create).collect(Collectors.toList());
    }

    public void updateQuality() {
        wrappedItems.forEach(ItemCalculator::updateQuality);
    }
}