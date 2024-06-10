package com.planner.travel.domain.user.entity;

import com.planner.travel.domain.profile.entity.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    private String email;

    private String password;

    private String nickname;

    private Long userTag;

    private LocalDate birthday;

//    private String phoneNumber;

    private LocalDateTime signupDate;

    private boolean isWithdrawal;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String provider;

    @Enumerated(value = EnumType.STRING)
    private Sex sex;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId")
    private Profile profile;


    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void updateSex(Sex sex) {
        this.sex = sex;
    }

//    public void updatePhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }

    public void updateWithdrawal(boolean isWithdrawal) {
        this.isWithdrawal = isWithdrawal;
    }
}

