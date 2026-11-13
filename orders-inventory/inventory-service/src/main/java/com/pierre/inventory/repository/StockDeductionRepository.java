package com.pierre.inventory.repository;

import com.pierre.inventory.entity.StockDeduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDeductionRepository extends JpaRepository<StockDeduction, Long> {
}
