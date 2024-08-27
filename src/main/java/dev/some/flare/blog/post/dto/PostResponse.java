package dev.some.flare.blog.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PostResponse {
    private String postId;
    private String authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
    private Long commentCount;
}
