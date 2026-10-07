package stellarburgers.dto;

public class OrderResponse {
    private boolean success;
    private String name;
    private Order order;
    private int price;
    private String message;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}