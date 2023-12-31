/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.sima.dms.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

import static com.sima.dms.domain.enums.MetadataFieldNameEnum.getFieldName;
import static java.util.Objects.isNull;

@Entity
@Table(name = "OKM_NODE_PROPERTY", uniqueConstraints = {@UniqueConstraint(name = "IDX_NOD_PROP_NODGRPNAM", columnNames = {"NPG_NODE", "NPG_GROUP", "NPG_NAME"})})
public class NodeProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NPG_NODE", nullable = false)
    @JsonIgnoreProperties(value = "nodeProperty", allowSetters = true)
    private NodeBase node;

    @Id
    @Column(name = "NPG_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "NPG_GROUP", length = 64, nullable = false)
    private String group;

    @Column(name = "NPG_NAME", length = 64, nullable = false)
    private String name;

    @Column(name = "NPG_VALUE", length = 512)
    private String value;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nodeProperty")
    private PropertyValidation validation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        if (!isNull(getFieldName(this.name)))
            return getFieldName(this.name).name();
        else return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NodeBase getNode() {
        return node;
    }

    public void setNode(NodeBase node) {
        this.node = node;
    }

    public PropertyValidation getValidation() {
        return validation;
    }

    public void setValidation(PropertyValidation validation) {
        validation.setNodeProperty(this);
        this.validation = validation;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id=").append(id);
        sb.append(", node=").append(node == null ? null : node.getUuid());
        sb.append(", group=").append(group);
        sb.append(", name=").append(name);
        sb.append(", value=").append(value);
        sb.append("}");
        return sb.toString();
    }
}
