package com.planner.travel.global.util.image.entity;

import com.planner.travel.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    private String imageUrl;

    private boolean isThumb;

    private LocalDateTime createdAt;

    private boolean isDeleted;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
