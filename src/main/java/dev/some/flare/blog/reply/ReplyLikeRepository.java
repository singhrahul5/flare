package dev.some.flare.blog.reply;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends MongoRepository<ReplyLike, ObjectId> {

    Optional<ReplyLike> findByReplyIdAndUserId(ObjectId replyId, Long userId);
}
