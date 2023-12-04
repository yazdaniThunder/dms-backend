package com.sima.dms.domain.entity;

import com.sima.dms.domain.enums.MetadataFieldNameEnum;
import com.sima.dms.domain.enums.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "DMS_METADATA_TREE")
public class MetadataTree implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "TRANSACTION_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "parent")
    private MetadataTree parent;

    @Column(name = "FIELD_NAME")
    @Enumerated(EnumType.ORDINAL)
    private MetadataFieldNameEnum fieldName;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL)
    private List<MetadataTree> children = new ArrayList<>();

    public MetadataTree(String name, MetadataTree parent) {
        this.name = name;
        this.parent = parent;
    }

    public void setChildren(List<MetadataTree> children) {
        this.children.clear();
        if (children != null) {
            children.forEach(child -> child.setParent(this));
            this.children.addAll(children);
        }
        this.children = children;
    }


}
