
public class EmailSender {
    public void sendEmail(String customerEmail, String subject, String message){
        System.out.println("Email to: " + customerEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + message);
    }
    public void sendConfirmationEmail(Order order) {
        String customerEmail = order.getCustomerEmail();
        String customerName = order.getCustomerName();
        String message = "Thank you for your order, " + customerName + "!\n\n" +
                "Your order details:\n";
        for (Item item : order.getItems()) {
            message += item.getName() + " - " + item.getPrice() + "\n";
        }
        message += "Total: " + calculateTotalPrice();
        sendEmail(customerEmail, "Order Confirmation", message);
    }
}
