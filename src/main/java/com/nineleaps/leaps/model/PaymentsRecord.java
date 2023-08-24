package com.nineleaps.leaps.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


}