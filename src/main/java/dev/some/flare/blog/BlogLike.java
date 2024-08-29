package dev.some.flare.blog;

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
        @CompoundIndex(name = "unique_user_blog_like", def = "{'blogId': 1, 'userId': 1}", unique = true)
})
@Document(collection = "blog_likes")
public class BlogLike {
    @Id
    private ObjectId id;

    @Indexed(name = "idx_blogId")
    private ObjectId blogId;

    private Long userId;

    @Builder
    public BlogLike(ObjectId blogId, Long userId) {
        this.blogId = blogId;
        this.userId = userId;
    }
}
