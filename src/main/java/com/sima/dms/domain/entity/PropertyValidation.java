package com.sima.dms.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "DMS_PROPERTY_VALIDATION")
public class PropertyValidation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SIMILARITY")
    private double similarity;

    @JoinColumn(name = "NODE_PROPERTY_ID")
    @OneToOne(cascade = CascadeType.ALL)
    private NodeProperty nodeProperty;

    public PropertyValidation(double similarity) {
        this.similarity = similarity;
    }
}
