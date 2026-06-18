package com.factoryflow.backend.workorders;

import com.factoryflow.backend.workorders.WorkOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkOrdersRepository extends JpaRepository<WorkOrders, String> {
    long countByStatus(String status);
    List<WorkOrders> findAllByOrderByOrderNumberAsc();
}
