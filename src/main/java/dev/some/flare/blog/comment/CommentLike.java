package dev.some.flare.blog.comment;

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
        @CompoundIndex(name = "unique_user_comment_like", def = "{'commentId': 1, 'userId': 1}", unique = true)
})
@Document(collection = "blog_comment_likes")
public class CommentLike {
    @Id
    private ObjectId id;
    @Indexed(name = "idx_commentId")
    private ObjectId commentId;
    private Long userId;

    @Builder
    public CommentLike(ObjectId commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }
}
