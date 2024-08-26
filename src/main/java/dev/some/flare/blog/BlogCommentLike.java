package dev.some.flare.blog;

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
public class BlogCommentLike {
    @Id
    private ObjectId id;
    @Indexed(name = "idx_commentId")
    private ObjectId commentId;
    private Long userId;
}
