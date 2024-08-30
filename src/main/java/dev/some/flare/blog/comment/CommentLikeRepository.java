package dev.some.flare.blog.comment;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommentLikeRepository extends MongoRepository<CommentLike, ObjectId> {

    Optional<CommentLike> findByCommentIdAndUserId(ObjectId commentId, Long userId);

}
