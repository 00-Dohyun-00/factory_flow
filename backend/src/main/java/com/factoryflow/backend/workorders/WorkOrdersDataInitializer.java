package com.factoryflow.backend.workorders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class WorkOrdersDataInitializer implements CommandLineRunner {

    private final WorkOrdersRepository workordersRepository;

    public WorkOrdersDataInitializer(WorkOrdersRepository workordersRepository) {
        this.workordersRepository = workordersRepository;
    }

    @Override
    public void run(String... args) {
        if (workordersRepository.count() > 0) return;

        workordersRepository.saveAll(List.of(
                new WorkOrders("1", "WO2024001", "플라스틱 부품 A",      1000,  750, "EQ001", "in_progress",   "2024-05-29",   "2024-05-29"),
                new WorkOrders("2", "WO2024002", "포장박스 세트",       500,  500, "EQ002", "completed",   "2024-05-26", "2024-05-26"),
                new WorkOrders("3", "WO2024003", "금속 부품 B",         200,  0, "EQ003", "waiting", "2024-05-28",      "2024-05-28"),
                new WorkOrders("4", "WO2024004", "검사완료 제품", 300,  0, "EQ004", "cancelled",   "2024-05-30",   "2024-05-30")
        ));
    }

}
