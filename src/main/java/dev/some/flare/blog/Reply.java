package dev.some.flare.blog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
//@Document(collection = "replies")
public class Reply {
    @Id
    private ObjectId id;
    private String replyId;
    private ObjectId onComment;
    private ObjectId onPost;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
    private Long likes;

}
