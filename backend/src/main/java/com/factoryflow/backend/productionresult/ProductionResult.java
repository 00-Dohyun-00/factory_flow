package com.factoryflow.backend.productionresult;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "production_result")
public class ProductionResult {

    @Id
    private String id;
    private String resultNumber;
    private String orderNumber;
    private String productName;
    private int productionQuantity;
    private int defectQuantity;
    private double yieldRate;
    private String equipment;
    private String workDate;

    public ProductionResult() {}

    public ProductionResult(String id, String resultNumber, String orderNumber, String productName, int productionQuantity, int defectQuantity, double yieldRate, String equipment, String workDate) {
        this.id = id;
        this.resultNumber = resultNumber;
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.productionQuantity = productionQuantity;
        this.defectQuantity = defectQuantity;
        this.yieldRate = yieldRate;
        this.equipment = equipment;
        this.workDate = workDate;
    }

    public String getId() { return id; }
    public String getResultNumber() { return resultNumber; }
    public String getOrderNumber() { return orderNumber; }
    public String getProductName() { return productName; }
    public int getProductionQuantity() { return productionQuantity; }
    public int getDefectQuantity() { return defectQuantity; }
    public double getYieldRate() { return yieldRate; }
    public String getEquipment() { return equipment; }
    public String getWorkDate() { return workDate; }

    public void setId(String id) { this.id = id; }
    public void setResultNumber(String resultNumber) { this.resultNumber = resultNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductionQuantity(int productionQuantity) { this.productionQuantity = productionQuantity; }
    public void setDefectQuantity(int defectQuantity) { this.defectQuantity = defectQuantity; }
    public void setYieldRate(double yieldRate) { this.yieldRate = yieldRate; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }
}