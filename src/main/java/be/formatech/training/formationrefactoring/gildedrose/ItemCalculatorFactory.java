package be.formatech.training.formationrefactoring.gildedrose;

public class ItemCalculatorFactory {

    public ItemCalculator create(Item item) {
        return new ItemCalculator(item);
    }
}
