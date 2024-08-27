package dev.some.flare.blog.post;

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
        @CompoundIndex(name = "unique_user_post_like", def = "{'postId': 1, 'userId': 1}", unique = true)
})
@Document(collection = "blog_post_likes")
public class PostLike {
    @Id
    private ObjectId id;

    @Indexed(name = "idx_postId")
    private ObjectId postId;

    private Long userId;
}
