package dev.some.flare.user;

import dev.some.flare.blog.post.Post;
import dev.some.flare.blog.post.PostController;
import dev.some.flare.blog.post.PostService;
import dev.some.flare.blog.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{username:^[a-z_][a-z0-9_]{0,19}$}/blogs/posts")
    public ResponseEntity<List<PostResponse>> getPaginatedUserBlogPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if(page <= 0)
            page = 1;

        List<PostResponse> posts = postService.findPostsByUsernameWithPagination(username, page, size);
        if (posts.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(posts);
    }

}
