package dev.some.flare.blog.reply;

import dev.some.flare.blog.Blog;
import dev.some.flare.blog.BlogService;
import dev.some.flare.blog.comment.Comment;
import dev.some.flare.blog.comment.CommentService;
import dev.some.flare.blog.dto.CreateReplyRequest;
import dev.some.flare.blog.dto.ReplyResponse;
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
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final RandomIdGeneratorService randomIdGeneratorService;
    private final BlogService blogService;
    private final CommentService commentService;
    private final UserService userService;
    private final ReplyLikeRepository replyLikeRepository;

    @Transactional
    public void createReply(
            CreateReplyRequest createReplyRequest, String externalBlogId,
            String externalCommentId, String username
    ) {
        User user = userService.loadUserByUsername(username);
        Blog blog = blogService.getBlogByExternalBlogId(externalBlogId);
        Comment comment = commentService.getCommentByExternalCommentIdAndBlogId(externalCommentId, blog.getId());

        String uniqueRandomId;
        int retries = 0;
        do {
            uniqueRandomId = randomIdGeneratorService.generateRandomId();
            try {
                if (!replyRepository.existsByExternalReplyId(uniqueRandomId)) {
                    replyRepository.save(Reply.builder()
                            .externalReplyId(uniqueRandomId)
                            .commentId(comment.getId())
                            .blogId(blog.getId())
                            .authorId(user.getId())
                            .content(createReplyRequest.getContent())
                            .createdAt(Instant.now())
                            .likeCount(0L)
                            .build()
                    );
                    commentService.incrementReplyCount(comment.getId());
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

    private ReplyResponse convertToReplyResponse(Reply reply) {
        String externalCommentId = commentService.getCommentById(reply.getCommentId()).getExternalCommentId();
        String externalBlogId = blogService.getBlogById(reply.getBlogId()).getExternalBlogId();
        String username = userService.getUserByUserId(reply.getAuthorId()).getUsername();
        return ReplyResponse.builder()
                .replyId(reply.getExternalReplyId())
                .commentId(externalCommentId)
                .blogId(externalBlogId)
                .authorId(username)
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .likeCount(reply.getLikeCount())
                .build();
    }

    public List<ReplyResponse> getPaginatedReply(String externalBlogId, String externalCommentId, int page, int size) {
        Blog blog = blogService.getBlogByExternalBlogId(externalBlogId);
        Comment comment = commentService.getCommentByExternalCommentIdAndBlogId(externalCommentId, blog.getId());
        return replyRepository.findByCommentIdAndBlogIdOrderByCreatedAtAsc(comment.getId(), blog.getId(),
                PageRequest.of(page - 1, size)
        ).stream().map(this::convertToReplyResponse).toList();
    }

    public Reply getByExternalReplyIdAndCommentIdAndBlogId(
            String externalReplyId, ObjectId commentId, ObjectId blogId
    ) {
        return replyRepository.findByExternalReplyIdAndCommentIdAndBlogId(externalReplyId, commentId, blogId)
                .orElseThrow(() -> new NotFoundException("Reply not found."));
    }

    private void likeReply(ObjectId replyId, Long userId) {
        replyLikeRepository.save(ReplyLike.builder()
                .replyId(replyId).userId(userId)
                .build()
        );
        replyRepository.incrementLikeCount(replyId);
    }

    private void unlikeReply(ObjectId replyLikeId, ObjectId replyId) {
        replyLikeRepository.deleteById(replyLikeId);
        replyRepository.decrementLikeCount(replyId);
    }

    @Transactional
    public void toggleLikeOnReply(
            String externalBlogId, String externalCommentId,
            String externalReplyId, String username
    ) {
        Blog blog = blogService.getBlogByExternalBlogId(externalBlogId);
        Comment comment = commentService.getCommentByExternalCommentIdAndBlogId(externalCommentId, blog.getId());
        Reply reply = getByExternalReplyIdAndCommentIdAndBlogId(externalReplyId, comment.getId(), blog.getId());
        User user = userService.loadUserByUsername(username);

        Optional<ReplyLike> optionalReplyLike = replyLikeRepository.findByReplyIdAndUserId(reply.getId(),
                user.getId());
        if (optionalReplyLike.isEmpty())
            likeReply(reply.getId(), user.getId());
        else
            unlikeReply(optionalReplyLike.get().getId(), reply.getId());
    }
}
