package dev.some.flare.blog;

import dev.some.flare.blog.comment.Comment;
import dev.some.flare.blog.comment.CommentService;
import dev.some.flare.blog.dto.BlogResponse;
import dev.some.flare.blog.dto.CommentResponse;
import dev.some.flare.blog.dto.CreateBlogRequest;
import dev.some.flare.blog.dto.CreateCommentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public void createNewBlog(@RequestBody @Valid CreateBlogRequest createBlogRequest, Authentication auth) {
        String username = auth.getName();
        blogService.createBlog(createBlogRequest, username);
    }

    @GetMapping("/{externalBlogId:^\\p{Alnum}{10}$}")
    public ResponseEntity<BlogResponse> retrieveBlogByExternalBlogId(@PathVariable String externalBlogId) {
        return ResponseEntity.ok(blogService.getBlogResponseByExternalBlogId(externalBlogId));
    }

    @GetMapping
    public ResponseEntity<List<BlogResponse>> getPaginatedBlogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page <= 0)
            page = 1;

        List<BlogResponse> blogs = blogService.findBlogsWithPagination(page, size);
        if (blogs.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(blogs);
    }

    @PostMapping("/{externalBlogId:^\\p{Alnum}{10}$}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public void createNewCommentOnBlog(@PathVariable String externalBlogId,
                                       @RequestBody CreateCommentRequest createCommentRequest,
                                       Authentication auth
    ) {
        commentService.createComment(createCommentRequest, externalBlogId, auth.getName());
    }

    @GetMapping("/{externalBlogId:^\\p{Alnum}{10}$}/comments")
    public ResponseEntity<List<CommentResponse>> getPaginatedCommentsOnBlog(
            @PathVariable String externalBlogId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page <= 0)
            page = 1;

        List<CommentResponse> comments = commentService.findCommentssWithPagination(externalBlogId, page, size);
        if (comments.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(comments);
    }
}
