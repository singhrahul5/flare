package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class PostLike {
    @Id
    private ObjectId id;
    private ObjectId onPost;
    private Long userId;
}
