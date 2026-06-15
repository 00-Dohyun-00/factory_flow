package com.factoryflow.backend.Inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "material")
public class Material {

    @Id
    private String id;
    private String code;
    private String name;
    private String category;
    private int currentStock;
    private int safetyStock;
    private String unit;
    private String status;
    private String lastMovementDate;

    public Material() {}

    public Material(String id, String code, String name, String category, int currentStock, int safetyStock, String unit, String status, String lastMovementDate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.currentStock = currentStock;
        this.safetyStock = safetyStock;
        this.unit = unit;
        this.status = status;
        this.lastMovementDate = lastMovementDate;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getCurrentStock() { return currentStock; }
    public int getSafetyStock() { return safetyStock; }
    public String getUnit() { return unit; }
    public String getStatus() { return status; }
    public String getLastMovementDate() { return lastMovementDate; }

    public void setId(String id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public void setSafetyStock(int safetyStock) { this.safetyStock = safetyStock; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStatus(String status) { this.status = status; }
    public void setLastMovementDate(String lastMovementDate) { this.lastMovementDate = lastMovementDate; }
}
