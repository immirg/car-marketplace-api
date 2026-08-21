package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarModelDTO {
    String brand;
    String model;
    Integer id;
}
