package dev.some.flare.blog;

import dev.some.flare.blog.dto.CreateBlogRequest;
import dev.some.flare.blog.dto.BlogResponse;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public void createNewBlogBlog(@RequestBody @Valid CreateBlogRequest createBlogRequest, Authentication auth) {
        String username = auth.getName();
        blogService.createBlog(createBlogRequest, username);
    }

    @GetMapping("/{externalBlogId:^\\p{Alnum}{10}$}")
    public ResponseEntity<BlogResponse> retrieveBlogByExternalBlogId(@PathVariable String externalBlogId) {
        return ResponseEntity.ok(blogService.getBlogByExternalBlogId(externalBlogId));
    }

    @GetMapping
    public ResponseEntity<List<BlogResponse>> getPaginatedBlogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if(page <= 0)
            page = 1;

        List<BlogResponse> blogs = blogService.findBlogsWithPagination(page, size);
        if (blogs.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(blogs);
    }
}
