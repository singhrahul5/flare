package dev.some.flare.blog.comment;

import dev.some.flare.blog.Blog;
import dev.some.flare.blog.BlogService;
import dev.some.flare.blog.dto.CommentResponse;
import dev.some.flare.blog.dto.CreateCommentRequest;
import dev.some.flare.exception.NotFoundException;
import dev.some.flare.user.User;
import dev.some.flare.user.UserService;
import dev.some.flare.utils.RandomIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogService blogService;
    private final UserService userService;
    private final RandomIdGeneratorService randomIdGeneratorService;
    private final CommentLikeRepository commentLikeRepository;

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

    public Comment getCommentByExternalCommentId(String externalCommnetId) {
        return commentRepository.findByExternalCommentId(externalCommnetId)
                .orElseThrow(() -> new NotFoundException("Comment not fount"));
    }

    public Comment getCommentByExternalCommentIdAndBlogId(String externalCommnetId, ObjectId blogId) {
        return commentRepository.findByExternalCommentIdAndBlogId(externalCommnetId, blogId)
                .orElseThrow(() -> new NotFoundException("Comment not fount"));
    }

    private void likeComment(ObjectId commentId, Long userId) {
        commentLikeRepository.save(CommentLike.builder()
                .commentId(commentId).userId(userId)
                .build()
        );
        commentRepository.incrementLikeCount(commentId);
    }

    private void unlikeComment(ObjectId commentLikeId, ObjectId commentId) {
        commentLikeRepository.deleteById(commentLikeId);
        commentRepository.decrementLikeCount(commentId);
    }

    @Transactional
    public void toggleLikeOnComment(String externalBlogId, String externalCommentId, String username) {
        Blog blog = blogService.getBlogByExternalBlogId(externalBlogId);
        Comment comment = getCommentByExternalCommentIdAndBlogId(externalCommentId, blog.getId());
        User user = userService.loadUserByUsername(username);

        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findByCommentIdAndUserId(comment.getId(),
                user.getId());
        if (optionalCommentLike.isEmpty())
            likeComment(comment.getId(), user.getId());
        else
            unlikeComment(optionalCommentLike.get().getId(), comment.getId());
    }

    public void incrementReplyCount(ObjectId id) {
        commentRepository.incrementReplyCount(id);
    }

    public Comment getCommentById(ObjectId id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment Not Found."));
    }
}
