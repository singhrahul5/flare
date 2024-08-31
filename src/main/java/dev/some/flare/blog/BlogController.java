package dev.some.flare.blog;

import dev.some.flare.blog.comment.CommentService;
import dev.some.flare.blog.dto.*;
import dev.some.flare.blog.reply.ReplyService;
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
    private final ReplyService replyService;

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


    @PostMapping("/{externalBlogId:^\\p{Alnum}{10}$}/like")
    @PreAuthorize("hasRole('USER')")
    public void likeOrUnlikeBlog(@PathVariable String externalBlogId, Authentication auth) {
        blogService.toggleLikeOnBlog(externalBlogId, auth.getName());
    }

    @PostMapping("/{externalBlogId:^\\p{Alnum}{10}$}/comments/{externalCommentId:^\\p{Alnum}{10}$}/like")
    @PreAuthorize("hasRole('USER')")
    public void likeOrUnlikeComment(@PathVariable String externalBlogId,
                                    @PathVariable String externalCommentId,
                                    Authentication auth
    ) {
        commentService.toggleLikeOnComment(externalBlogId, externalCommentId, auth.getName());
    }

    @PostMapping("/{externalBlogId:^\\p{Alnum}{10}$}/comments/{externalCommentId:^\\p{Alnum}{10}$}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public void createNewReplyOnComment(
            @PathVariable String externalBlogId, @PathVariable String externalCommentId,
            @RequestBody CreateReplyRequest createReplyRequest, Authentication auth
    ) {
        replyService.createReply(createReplyRequest, externalBlogId, externalCommentId, auth.getName());
    }

    @GetMapping("/{externalBlogId:^\\p{Alnum}{10}$}/comments/{externalCommentId:^\\p{Alnum}{10}$}/replies")
    public ResponseEntity<List<ReplyResponse>> getRepliesOnComment(
            @PathVariable String externalBlogId, @PathVariable String externalCommentId,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size
    ) {
        if (page <= 0)
            page = 1;
        if (size <= 1)
            size = 5;

        List<ReplyResponse> replyResponses = replyService.getPaginatedReply(externalBlogId, externalCommentId, page,
                size);

        if (replyResponses.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(replyResponses);
    }

    @PostMapping("/{externalBlogId:^\\p{Alnum}{10}$}/comments/{externalCommentId:^\\p{Alnum}{10}$}/replies" +
            "/{externalReplyId:^\\p{Alnum}{10}$}/like")
    @PreAuthorize("hasRole('USER')")
    public void likeOrUnlikeReply(
            @PathVariable String externalBlogId, @PathVariable String externalCommentId,
            @PathVariable String externalReplyId, Authentication auth
    ) {
        replyService.toggleLikeOnReply(externalBlogId, externalCommentId, externalReplyId, auth.getName());
    }
}
