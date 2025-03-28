import java.util.List;

public class Order {
    private List<Item> items;
    private String customerName;
    private String customerEmail;

    public Order(List<Item> items, String customerName, String customerEmail) {
        this.items = items;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public double calculateTotalPrice() {
        double total = 0.0;
        final double FIXED_DISCOUNT = 10.0;
        final double PERCENT_DISCOUNT = 0.9;
        for (Item item : items) {
            double price = item.getPrice();
            if (item.getDiscountType() == DiscountType.PERCENTAGE) {
                price -= item.getDiscountAmount() * price;
            } else {
                price -= item.getDiscountAmount();
            }
            total += price * item.getQuantity();
        }
        if (GiftCardItem.hasGiftCard(items)) {
            total -= FIXED_DISCOUNT; // subtract $10 for gift card
        }
        if (total > 100.0) {
            total *= PERCENT_DISCOUNT; // apply 10% discount for orders over $100
        }
        return total;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void printOrder() {
        System.out.println("Order Details:");
        for (Item item : items) {
            System.out.println(item.getName() + " - " + item.getPrice());
        }
    }

    public void addItemsFromAnotherOrder(Order otherOrder) {
        for (Item item : otherOrder.getItems()) {
            items.add(item);
        }
    }

}

