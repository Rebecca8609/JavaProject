package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TransportationMethod")
public class ItemTransportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transportationId;

    private String name; // 運送方式名稱
}