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
}
