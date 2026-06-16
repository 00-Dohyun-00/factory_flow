package com.factoryflow.backend.productionresult;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

public interface ProductionResultRepository extends JpaRepository<ProductionResult, String> {
    @Query("SELECT COALESCE(SUM(p.productionQuantity), 0) FROM ProductionResult p WHERE p.workDate = :date")
    long sumProductionQuantityByDate(String date);
}