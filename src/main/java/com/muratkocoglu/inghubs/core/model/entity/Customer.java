package com.muratkocoglu.inghubs.core.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

	@Column(nullable = false)
    private String name;
	
	@Column(nullable = false)
    private String surname;
	
	@Column(nullable = false)
    private String nationalId;
}
