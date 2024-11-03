
public class TaxableItem extends Item {
    private double taxRate = 7;
    
    public TaxableItem(String name, double price, int quantity, DiscountType discountType, double discountAmount){
        super(name, price, quantity, discountType, discountAmount);
        price = applyTaxRate(price);
    }


    public double applyTaxRate(double price) {
        double tax = taxRate / 100.0 * price;
        price += tax;
        return  price;
    }
}
