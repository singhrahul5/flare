package dev.some.flare.blog.comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "blog_comments")
public class Comment {
    @Id
    private ObjectId id;
    @Indexed(name = "unique_externalCommentId")
    private String externalCommentId;
    private ObjectId postId;
    private Long authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
    private Long replyCount;

}
