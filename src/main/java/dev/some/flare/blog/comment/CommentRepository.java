package dev.some.flare.blog.comment;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, ObjectId>, CustomCommentRepository {

    boolean existsByExternalCommentId(String externalCommentId);

    Optional<Comment> findByExternalCommentId(String externalCommentId);

    Optional<Comment> findByExternalCommentIdAndBlogId(String externalCommentId, ObjectId blogId);

}
