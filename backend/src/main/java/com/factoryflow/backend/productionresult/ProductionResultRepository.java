package com.factoryflow.backend.productionresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductionResultRepository extends JpaRepository<ProductionResult, String> {
    @Query("SELECT COALESCE(SUM(p.productionQuantity), 0) FROM ProductionResult p WHERE p.workDate = :date")
    long sumProductionQuantityByDate(String date);

    @Query("SELECT p.workDate, SUM(p.productionQuantity) FROM ProductionResult p GROUP BY p.workDate ORDER BY p.workDate")
    List<Object[]> findDailyProduction();

    @Query("SELECT p.workDate, AVG(p.yieldRate) FROM ProductionResult p GROUP BY p.workDate ORDER BY p.workDate")
    List<Object[]> findDailyYieldRate();

    List<ProductionResult> findAllByOrderByWorkDateDesc();
}