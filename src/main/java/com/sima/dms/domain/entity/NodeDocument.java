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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Indexed
@NoArgsConstructor
@Table(name = "OKM_NODE_DOCUMENT")
public class NodeDocument extends NodeBase {

    private static final long serialVersionUID = 1L;
    public static final String TEXT_FIELD = "text";

    @Id
    @Column(name = "NBS_UUID", length = 64)
    protected String uuid;

    @Column(name = "NDC_LAST_MODIFIED")
    private Calendar lastModified;

    @Column(name = "NDC_LANGUAGE", length = 8)
    private String language;

    @Column(name = "NDC_TITLE", length = 256)
    private String title;

    @Column(name = "NDC_DESCRIPTION", length = 2048)
    private String description;

    @Column(name = "NDC_MIME_TYPE", length = 128)
    private String mimeType;

    @Lob
    @Column(name = "NDC_TEXT")
    private String text;

    @Column(name = "NDC_CHECKED_OUT", nullable = false)
    @Type(type = "true_false")
    private boolean checkedOut;

    @Column(name = "NDC_ENCRYPTION", nullable = false)
    @Type(type = "true_false")
    private boolean encryption;

    @Column(name = "NDC_CIPHER_NAME")
    private String cipherName;

    @Column(name = "NDC_SIGNED", nullable = false)
    @Type(type = "true_false")
    private boolean signed;

    @Column(name = "NDC_TEXT_EXTRACTED", nullable = false)
    @Type(type = "true_false")
    private boolean textExtracted;

    @Column(name = "NDC_LOCKED", nullable = false)
    @Type(type = "true_false")
    private boolean locked;

    @Embedded
    private NodeLock lock;

    @ElementCollection
    @Column(name = "NT_THUMBNAIL")
    @CollectionTable(name = "OKM_NODE_THUMBNAIL", joinColumns = {@JoinColumn(name = "NT_DOCUMENT")})
    protected Set<String> thumbnails = new HashSet<String>();
}
