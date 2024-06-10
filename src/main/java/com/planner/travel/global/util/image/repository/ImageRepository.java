package com.planner.travel.global.util.image.repository;

import com.planner.travel.global.util.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
