package domain.model;

import java.util.UUID;

public class Customer {
    private String id;
    private String name;
    private String address;
    private String phone;

    public Customer(String name, String address, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}