package com.planner.travel.domain.profile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profileImageUrl;

    public Profile withProfileImageUrl(String profileImageUrl) {
        return Profile.builder()
                .id(this.id)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
