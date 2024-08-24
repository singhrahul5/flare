package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class CommentLike {
    @Id
    private ObjectId id;
    private ObjectId onComment;
    private Long userId;
}
