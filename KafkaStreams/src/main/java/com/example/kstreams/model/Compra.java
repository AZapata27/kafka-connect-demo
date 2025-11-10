package com.example.kstreams.model;

public class Compra {
    private Integer id;
    private Integer userId;
    private Integer value;
    private Integer orderId;

    public Compra() {}

    public Compra(Integer id, Integer userId, Integer value, Integer orderId) {
        this.id = id;
        this.userId = userId;
        this.value = value;
        this.orderId = orderId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    @Override
    public String toString() {
        return "Compra{" +
                "id=" + id +
                ", userId=" + userId +
                ", value=" + value +
                ", orderId=" + orderId +
                '}';
    }
}
