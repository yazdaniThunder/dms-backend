package com.sima.dms.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sima.dms.domain.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@Table(name = "DMS_PROFILE")
public class Profile implements Serializable {

    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "pr_ac_branch")
    private Long prAcBranch;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_code")
    private Long branchCode;

    @Column(name = "parent_branch")
    private String parentBranch;

    @Column(name = "job")
    private String job;

    @Column(name = "position")
    private String position;

    @Column(name = "position_code")
    private String positionCode;


    @Column(name = "ranking_job_desc")
    private String rankingJobDesc;

    @Column(name = "ranking_position_desc")
    private String rankingPositionDesc;

    @Column(name = "ranking_position_code")
    private Long rankingPositionCode;

    @Column(name = "role")
    @Enumerated(EnumType.ORDINAL)
    private RoleEnum role;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean active = true;

    @JsonIgnore
    @ManyToMany(mappedBy = "assignedProfiles",fetch = FetchType.EAGER)
    private List<Branch> assignedBranches = new ArrayList<>();

    public Profile(Long id) {
        this.id = id;
    }

    public Profile(String username) {
        User user = new User();
        user.setPersonelUserName(username);
        this.user = user;
        this.role=RoleEnum.BU;
        this.active =true;
    }

    @Override
    public Profile clone() {
        Profile profile = new Profile();
        profile.setUser(this.user);
        profile.setBranch(this.branch);
        profile.setRole(this.role);
        profile.setPrAcBranch(this.prAcBranch);
        profile.setBranchCode(this.branchCode);
        profile.setBranchName(this.branchName);
        profile.setPosition(this.position);
        profile.setPositionCode(this.positionCode);
        profile.setParentBranch(this.parentBranch);
        profile.setRankingJobDesc(this.rankingJobDesc);
        profile.setRankingPositionCode(this.rankingPositionCode);
        profile.setRankingPositionDesc(this.rankingPositionDesc);
        profile.setAssignedBranches(new ArrayList<>(this.assignedBranches));
        profile.setJob(this.job);
        return profile;
    }
}
