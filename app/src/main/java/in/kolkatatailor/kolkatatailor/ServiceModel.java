package in.kolkatatailor.kolkatatailor;

public class ServiceModel {

    private String servicename;
    private int price,quantity,total;

    ServiceModel(){}

    ServiceModel(String servicename,int price,int quantity,int total){

        this.servicename=servicename;
        this.price=price;
        this.quantity=quantity;
        this.total=total;


    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPrice() {

        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
