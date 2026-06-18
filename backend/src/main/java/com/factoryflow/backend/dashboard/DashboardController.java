package com.factoryflow.backend.dashboard;

import com.factoryflow.backend.Inventory.MaterialRepository;
import com.factoryflow.backend.equipment.EquipmentRepository;
import com.factoryflow.backend.productionresult.ProductionResultRepository;
import com.factoryflow.backend.workorders.WorkOrdersRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final EquipmentRepository equipmentRepository;
    private final WorkOrdersRepository workOrdersRepository;
    private final MaterialRepository materialRepository;
    private final ProductionResultRepository productionResultRepository;

    public DashboardController(
        EquipmentRepository equipmentRepository,
        WorkOrdersRepository workOrdersRepository,
        MaterialRepository materialRepository,
        ProductionResultRepository productionResultRepository
    ) {
        this.equipmentRepository = equipmentRepository;
        this.workOrdersRepository = workOrdersRepository;
        this.materialRepository = materialRepository;
        this.productionResultRepository = productionResultRepository;
    }

    @GetMapping("/kpi")
    public Map<String, Long> getKpi() {
        String today = LocalDate.now().toString();

        return Map.of(
            "todayProduction",  productionResultRepository.sumProductionQuantityByDate(today),
            "activeWorks",      workOrdersRepository.countByStatus("in_progress"),
            "equipmentErrors",  equipmentRepository.countByStatus("error"),
            "lowStockItems",    materialRepository.countByStatusIn(List.of("low", "critical"))
        );
    }

    @GetMapping("/daily-production")
    public List<Map<String, Object>> getDailyProduction() {
        return productionResultRepository.findDailyProduction().stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", row[0].toString());
                    item.put("quantity", ((Number) row[1]).longValue());
                    return item;
                })
                .toList();
    }

    @GetMapping("/equipment-status")
    public Map<String, Long> getEquipmentStatus() {
        return Map.of(
            "active",      equipmentRepository.countByStatus("active"),
            "stopped",     equipmentRepository.countByStatus("stopped"),
            "maintenance", equipmentRepository.countByStatus("maintenance"),
            "error",       equipmentRepository.countByStatus("error")
        );
    }

    @GetMapping("/yield-trend")
    public List<Map<String, Object>> getYieldTrend() {
        return productionResultRepository.findDailyYieldRate().stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", row[0].toString());
                    item.put("yieldRate", ((Number) row[1]).doubleValue());
                    return item;
                })
                .toList();
    }

    @GetMapping("/workorder-status")
    public Map<String, Long> getWorkOrderStatus() {
        return Map.of(
            "waiting",     workOrdersRepository.countByStatus("waiting"),
            "in_progress", workOrdersRepository.countByStatus("in_progress"),
            "completed",   workOrdersRepository.countByStatus("completed"),
            "cancelled",   workOrdersRepository.countByStatus("cancelled")
        );
    }
}
