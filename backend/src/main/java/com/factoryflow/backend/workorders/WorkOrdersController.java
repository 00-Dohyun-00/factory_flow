package com.factoryflow.backend.workorders;

import com.factoryflow.backend.Inventory.Material;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping
    public WorkOrders createWorkOrdersList(@RequestBody WorkOrders workorders) {
        return workordersRepository.save(workorders);
    }

    @PutMapping("/{id}")
    public WorkOrders updateWorkOrders(@PathVariable String id, @RequestBody WorkOrders workorders) {
        workorders.setId(id);
        return workordersRepository.save(workorders);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkOrders(@PathVariable String id) {
        workordersRepository.deleteById(id);
    }

}
