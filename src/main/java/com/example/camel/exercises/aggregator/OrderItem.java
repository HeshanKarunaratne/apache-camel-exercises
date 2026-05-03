package com.example.camel.exercises.aggregator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Heshan Karunaratne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class OrderItem {
    private int orderId;
    private String item;
}