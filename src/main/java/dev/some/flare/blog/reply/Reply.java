package dev.some.flare.blog.reply;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "blog_replies")
public class Reply {
    @Id
    private ObjectId id;
    @Indexed(name = "unique_replyId")
    private String externalReplyId;
    private ObjectId commentId;
    private ObjectId blogId;
    private Long authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;

    @Builder
    public Reply(String externalReplyId, ObjectId commentId, ObjectId blogId, Long authorId, String content,
                 Instant createdAt, Long likeCount
    ) {
        this.externalReplyId = externalReplyId;
        this.commentId = commentId;
        this.blogId = blogId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }
}
