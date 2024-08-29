package dev.some.flare.blog.comment;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, ObjectId>, CustomCommentRepository {

    boolean existsByExternalCommentId(String externalCommentId);
}
