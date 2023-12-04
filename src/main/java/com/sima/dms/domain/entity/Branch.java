package com.sima.dms.domain.entity;

import com.sima.dms.domain.dto.response.AssignedBranchDto;
import com.sima.dms.domain.enums.BranchTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NamedNativeQuery(
        name = "find_assigned_branch_dto",
        query = "select b.ID as id, b.BRANCH_NAME as branchName, b.BRANCH_CODE as branchCode, b.TYPE as type from DMS_BRANCH b join DMS_BRANCH_PROFILE dbp on b.ID = dbp.BRANCH_ID where dbp.PROFILE_ID =:profileId",
        resultSetMapping = "assigned_branch_dto"
)
@SqlResultSetMapping(
        name = "assigned_branch_dto",
        classes = @ConstructorResult(
                targetClass = AssignedBranchDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "branchName", type = String.class),
                        @ColumnResult(name = "branchCode", type = Long.class),
                        @ColumnResult(name = "type", type = Integer.class)
                }
        )
)

@Data
@Entity
@NoArgsConstructor
@Table(name = "DMS_BRANCH")
public class Branch  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "SEQUENCE", columnDefinition = "bigint default 1")
    private Long sequence = 1L;

    @Column(name = "branch_code", unique = true)
    private Long branchCode;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_type")
    private String branchType;

    @Enumerated(EnumType.ORDINAL)
    private BranchTypeEnum type;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "english_branch_name")
    private String englishBranchName;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "superVisor_code")
    private String superVisorCode;

    @Column(name = "superVisor_name")
    private String superVisorName;

    @Column(name = "branch_address")
    private String branchAddress;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "path" , length = 256)
    private String path;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Branch parent;

    @ManyToMany
    @JoinTable(name = "DMS_BRANCH_PROFILE",
            joinColumns = {@JoinColumn(name = "BRANCH_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PROFILE_ID")})
    private List<Profile> assignedProfiles = new ArrayList<>();

    public Branch(String name, Long code) {
        this.branchName = name;
        this.branchCode = code;
    }
}
