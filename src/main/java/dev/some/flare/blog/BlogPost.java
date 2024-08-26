package dev.some.flare.blog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "posts")
public class BlogPost {
    @Id
    private ObjectId id;
    @Indexed(name = "unique_externalPostId")
    private String externalPostId;
    private Long authorId;
    private String content;
    private Instant createdAt;
    private Long likeCount;
    private Long commentCount;


}
