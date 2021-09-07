package com.arnoldvaz27.doctors;

public class Medicines {
    public String name,stock,stockArrived,stockExpiry;

    public Medicines()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStockArrived() {
        return stockArrived;
    }

    public void setStockArrived(String stockArrived) {
        this.stockArrived = stockArrived;
    }

    public String getStockExpiry() {
        return stockExpiry;
    }

    public void setStockExpiry(String stockExpiry) {
        this.stockExpiry = stockExpiry;
    }
}
