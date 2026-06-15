package com.factoryflow.backend.workorders;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workorders")
@CrossOrigin(origins = "*")

public class WorkOrdersController {

    private final WorkOrdersRepository workordersRepository;

    public WorkOrdersController(WorkOrdersRepository workordersRepository) {
        this.workordersRepository = workordersRepository;
    }

    @GetMapping
    public List<WorkOrders> getWorkOrdersList() {
        return workordersRepository.findAll();
    }

}
