package dev.some.flare.blog.post;

import dev.some.flare.blog.post.dto.CreatePostRequest;
import dev.some.flare.blog.post.dto.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs/posts")
@RequiredArgsConstructor
public class PostController {

    final PostService postService;

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
}
