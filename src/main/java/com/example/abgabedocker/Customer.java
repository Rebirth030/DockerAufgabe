package com.example.abgabedocker;

public class Customer {
    String customerId,workloadId,eventType;
    Long timestamp;

    public Customer(String customerId, String workloadId, Long timestamp, String eventType){
        this.customerId = customerId;
        this.workloadId = workloadId;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }
    public String getCustomerId() {
        return customerId;
    }

    public String getWorkloadId() {
        return workloadId;
    }

    public String getEventType() {
        return eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
