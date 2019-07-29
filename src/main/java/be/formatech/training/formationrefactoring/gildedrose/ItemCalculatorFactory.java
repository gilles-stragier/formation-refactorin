package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculatorFactory {

    public ItemCalculator create(Item item) {
        switch (item.name) {
            case "Aged Brie":
                return new AgedBrieCalculator(item);
            case "Backstage passes to a TAFKAL80ETC concert":
                return new BackstagePassCalculator(item);
            case "Sulfuras, Hand of Ragnaros":
                return new SulfurasCalculator(item);
            default:
                return new ItemCalculator(item);
        }

    }
}
