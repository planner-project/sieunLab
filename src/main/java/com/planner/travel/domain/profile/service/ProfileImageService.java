package com.planner.travel.domain.profile.service;

import com.planner.travel.domain.profile.entity.Profile;
import com.planner.travel.domain.profile.repository.ProfileRepository;
import com.planner.travel.domain.user.component.UserFinder;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.util.image.entity.Category;
import com.planner.travel.global.util.image.service.ImageUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final ImageUpdateService updateService;
    private final UserFinder userFinder;
    private final ProfileRepository profileRepository;

    public void update(Long userId, MultipartFile multipartFile) throws Exception {
        Path path = updateService.saveImage(userId, multipartFile);
        User user = userFinder.find(userId);
        Profile profile = user.getProfile();

        profile = profile.withProfileImageUrl(path.toString());

        profileRepository.save(profile);
    }

    public void delete(Long userId) {
        User user = userFinder.find(userId);
        Profile profile = user.getProfile();

        profile = profile.withProfileImageUrl("Default");

        profileRepository.save(profile);
    }
}
