package dev.some.flare.blog.post;

import dev.some.flare.blog.post.dto.CreatePostRequest;
import dev.some.flare.blog.post.dto.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/blogs/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public void createNewBlogPost(@RequestBody @Valid CreatePostRequest createPostRequest, Authentication auth) {
        String username = auth.getName();
        postService.createPost(createPostRequest, username);
    }

    @GetMapping("/{externalPostId:^\\p{Alnum}{10}$}")
    public ResponseEntity<PostResponse> retrievePostByExternalPostId(@PathVariable String externalPostId) {
        return ResponseEntity.ok(postService.getPostByExternalPostId(externalPostId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPaginatedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if(page <= 0)
            page = 1;

        List<PostResponse> posts = postService.findPostsWithPagination(page, size);
        if (posts.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(posts);
    }
}
