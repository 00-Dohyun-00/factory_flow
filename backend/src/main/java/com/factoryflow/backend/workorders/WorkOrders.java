package com.factoryflow.backend.workorders;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "work_orders")
public class WorkOrders {

    @Id
    private String id;
    private String orderNumber;
    private String productName;
    private int targetQuantity;
    private int completedQuantity;
    private String equipment;
    private String status;
    private String startDate;
    private String dueDate;

    public WorkOrders() {}

    public WorkOrders(String id, String orderNumber, String productName, int targetQuantity, int completedQuantity, String equipment, String status, String startDate, String dueDate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.targetQuantity = targetQuantity;
        this.completedQuantity = completedQuantity;
        this.equipment = equipment;
        this.status = status;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getProductName() { return productName; }
    public int getTargetQuantity() { return targetQuantity; }
    public int getCompletedQuantity() { return completedQuantity; }
    public String getEquipment() { return equipment; }
    public String getStatus() { return status; }
    public String getStartDate() { return startDate; }
    public String getDueDate() { return dueDate; }

    public void setId(String id) { this.id = id; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setTargetQuantity(int targetQuantity) { this.targetQuantity = targetQuantity; }
    public void setCompletedQuantity(int completedQuantity) { this.completedQuantity = completedQuantity; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setStatus(String status) { this.status = status; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}
