package com.planner.travel.domain.profile.service;

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
    private final UserRepository userRepository;
    private final ImageUpdateService updateService;
    private final UserFinder userFinder;

    public void update(Long userId, MultipartFile multipartFile) throws Exception {
        Path path = updateService.saveImage(userId, multipartFile);
        User user = userFinder.find(userId);

        user.getProfile()
                .getImage()
                .updateImageUrl(path.toString());

        userRepository.save(user);
    }

    public void delete(Long userId) {
        User user = userFinder.find(userId);

        user.getProfile()
                .getImage()
                .updateImageUrl("");

        userRepository.save(user);
    }
}
