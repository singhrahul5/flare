package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class ReplyLike {
    @Id
    private ObjectId id;
    private ObjectId onReply;
    private Long userId;
}
