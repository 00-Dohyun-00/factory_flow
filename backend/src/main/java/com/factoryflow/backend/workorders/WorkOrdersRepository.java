package com.factoryflow.backend.workorders;

import com.factoryflow.backend.workorders.WorkOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrdersRepository extends JpaRepository<WorkOrders, String> {
}
