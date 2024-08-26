package dev.some.flare.blog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
//@Document(collection = "posts")
public class Post {
    @Id
    private ObjectId id;
    private String postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
    private Long likes;
    private Long comments;


}
