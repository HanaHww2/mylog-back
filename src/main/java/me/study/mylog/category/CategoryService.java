package me.study.mylog.category;

import lombok.RequiredArgsConstructor;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserService userService;
    private final CategoryRepository logCateRepo;

    @Transactional(readOnly=true)
    public List<CategoryResponseDto> getCategoriesByUserEmail(String userEmail) {

        User user = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("No Valid User"));

        return logCateRepo.findByUser(user)
                .stream()
                .map(CategoryResponseDto::new)
                .collect(Collectors.toList());
    }
}
