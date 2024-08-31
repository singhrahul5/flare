package dev.some.flare.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReplyResponse {
    private String replyId;
    private String commentId;
    private String blogId;
    private String authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
}
