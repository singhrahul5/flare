package dev.some.flare.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommentResponse {
    private String commentId;
    private String blogId;
    private String authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
    private Long replyCount;
}
