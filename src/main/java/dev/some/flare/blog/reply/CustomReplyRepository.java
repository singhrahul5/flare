package dev.some.flare.blog.reply;

import org.bson.types.ObjectId;

public interface CustomReplyRepository {

    void incrementLikeCount(ObjectId id);

    void decrementLikeCount(ObjectId id);
}
