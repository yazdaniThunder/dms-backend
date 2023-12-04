package com.sima.dms.domain.entity;

import com.sima.dms.domain.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Indexed
@NoArgsConstructor
@Table(name = "DMS_USER")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {

    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "cf_ci_no")
    private Long cfCiNo;

    @Column(name = "prsn_code")
    private Long personCode;

    @Column(name = "national_key")
    private String nationalKey;

    @Column(name = "personnel_user_name")
    private String personelUserName;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "complete_name")
    private String completeName;
    @Column(name = "father_name")
    private String fatherName;
    @Column(name = "is_int_active_code")
    private boolean isIntActiveCode;
    @Column(name = "is_int_active_desc")
    private String isIntActiveDesc;
    @Column(name = "active")
    private boolean active;

//    @Column(name = "PASSWORD")
//    private String password ;

    public User(Long id) {
        this.id = id;
    }

    public User(UserDto user) {
        this.cfCiNo = user.getCfCiNo();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.completeName = user.getCompleteName();
        this.fatherName = user.getFatherName();
        this.personelUserName = user.getPersonelUserName();
        this.personCode = user.getPersonCode();
        this.isIntActiveCode = user.isIntActiveCode();
       // this.password = user.getPassword();
    }

//    @PrePersist
//    protected void created() {
//        this.password = ENCODER.encode(password);
//    }

//    public void updatePassword(String newPassword) {
//        this.password = ENCODER.encode(newPassword);
//    }
//
//    public Boolean validatePassword(String password) {
//        return ENCODER.matches(password, this.password);
//    }

}
