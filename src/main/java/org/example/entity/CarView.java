package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "car_view")
@NoArgsConstructor
public class CarView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
    @Column(nullable = false)
    private LocalDateTime viewedAt;
}
