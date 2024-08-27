package dev.some.flare.blog.reply;

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
    private ObjectId postId;
    private Long authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
}
