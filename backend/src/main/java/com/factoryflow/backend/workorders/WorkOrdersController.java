package com.factoryflow.backend.workorders;

import com.factoryflow.backend.Inventory.Material;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        if (!workordersRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 작업지시를 찾을 수 없습니다: " + id);
        }
        workorders.setId(id);
        return workordersRepository.save(workorders);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkOrders(@PathVariable String id) {
        if (!workordersRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 작업지시를 찾을 수 없습니다: " + id);
        }

        workordersRepository.deleteById(id);
    }

}
