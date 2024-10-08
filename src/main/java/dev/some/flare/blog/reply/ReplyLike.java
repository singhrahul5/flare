package dev.some.flare.blog.reply;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "unique_user_reply_like", def = "{'replyId': 1, 'userId': 1}", unique = true)
})
@Document(collection = "blog_reply_likes")
public class ReplyLike {
    @Id
    private ObjectId id;
    @Indexed(name = "idx_replyId")
    private ObjectId replyId;
    private Long userId;

    @Builder
    public ReplyLike(ObjectId replyId, Long userId) {
        this.replyId = replyId;
        this.userId = userId;
    }
}
