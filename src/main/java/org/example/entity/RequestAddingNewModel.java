package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "new-model")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestAddingNewModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String producer;
    private String model;

    public RequestAddingNewModel(String producer, String model) {
        this.producer = producer;
        this.model = model;
    }
}
