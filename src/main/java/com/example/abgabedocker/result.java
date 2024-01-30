package com.example.abgabedocker;

public class result {
    private String customerId;
    private Long consumption;

    public String getCustomerId() {
        return customerId;
    }

    public Long getConsumption() {
        return consumption;
    }

    public void setConsumption(Long consumption) {
        this.consumption = consumption;
    }

    public result(String customerId, Long consumption) {
        this.customerId = customerId;
        this.consumption = consumption;
    }
}
