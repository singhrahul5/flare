package dev.some.flare.user;

import dev.some.flare.blog.BlogService;
import dev.some.flare.blog.dto.BlogResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;
    private final BlogService blogService;

    @GetMapping("/{username:^[a-z_][a-z0-9_]{0,19}$}/blogs")
    public ResponseEntity<List<BlogResponse>> getPaginatedUserBlogs(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page <= 0)
            page = 1;

        List<BlogResponse> blogs = blogService.findBlogsByUsernameWithPagination(username, page, size);
        if (blogs.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(blogs);
    }

}
