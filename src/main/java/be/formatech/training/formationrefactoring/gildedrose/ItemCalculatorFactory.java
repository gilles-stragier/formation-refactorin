package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculatorFactory {

    public ItemCalculator create(Item item) {
        switch (item.name) {
            case "Aged Brie":
                return new AgedBrieCalculator(item);
            default:
                return new ItemCalculator(item);
        }

    }
}
