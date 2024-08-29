package dev.some.flare.blog;

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
@Document(collection = "blogs")
public class Blog {
    @Id
    private ObjectId id;
    @Indexed(name = "unique_externalBlogId")
    private String externalBlogId;
    private Long authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
    private Long commentCount;

    @Builder
    public Blog(String externalBlogId, Long authorId, String content, Instant createdAt, Long likeCount,
                Long commentCount) {
        this.externalBlogId = externalBlogId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
