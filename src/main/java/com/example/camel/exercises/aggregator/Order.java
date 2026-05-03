package com.example.camel.exercises.aggregator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Heshan Karunaratne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {
    private int orderId;
    private List<String> items;
}