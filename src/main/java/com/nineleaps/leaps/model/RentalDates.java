package com.nineleaps.leaps.model;

import javax.persistence.*;

@Entity
@Table(name  = "rental_dates")
public class RentalDates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
