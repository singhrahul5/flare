package dev.some.flare.blog.comment;

import dev.some.flare.blog.Blog;
import dev.some.flare.blog.BlogService;
import dev.some.flare.blog.dto.CommentResponse;
import dev.some.flare.blog.dto.CreateCommentRequest;
import dev.some.flare.user.User;
import dev.some.flare.user.UserService;
import dev.some.flare.utils.RandomIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogService blogService;
    private final UserService userService;
    private final RandomIdGeneratorService randomIdGeneratorService;

    @Transactional
    public void createComment(CreateCommentRequest createCommentRequest, String externalBlogId, String username) {
        User user = userService.loadUserByUsername(username);
        Blog blog = blogService.getBlogByExternalBlogId(externalBlogId);

        String uniqueRandomId;
        int retries = 0;
        do {
            uniqueRandomId = randomIdGeneratorService.generateRandomId();
            try {
                if (!commentRepository.existsByExternalCommentId(uniqueRandomId)) {
                    Comment newComment = Comment.builder()
                            .externalCommentId(uniqueRandomId)
                            .blogId(blog.getId())
                            .authorId(user.getId())
                            .content(createCommentRequest.getContent())
                            .createdAt(Instant.now())
                            .likeCount(0L)
                            .replyCount(0L)
                            .build();
                    commentRepository.save(newComment);
                    blogService.incrementCommentCount(blog.getId());
                    return;
                }
            } catch (OptimisticLockingFailureException e) {
                retries++;
                if (retries >= 3) // retry limit
                    throw e;
            }
        } while (true);
    }

    public CommentResponse convertToCommentResponse(Comment comment) {
        User user = userService.getUserByUserId(comment.getAuthorId());
        Blog blog = blogService.getBlogById(comment.getBlogId());
        return CommentResponse.builder()
                .commentId(comment.getExternalCommentId())
                .blogId(blog.getExternalBlogId())
                .authorId(user.getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .replyCount(comment.getReplyCount())
                .build();
    }

    public List<CommentResponse> findCommentssWithPagination(String externalBlogId, int page, int size) {
        Blog blog = blogService.getBlogByExternalBlogId(externalBlogId);
        return commentRepository.getPopuralComments(blog.getId(), PageRequest.of(page - 1, size))
                .stream().map(this::convertToCommentResponse).toList();
    }
}
