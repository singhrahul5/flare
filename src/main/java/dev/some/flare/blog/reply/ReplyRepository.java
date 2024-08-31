package dev.some.flare.blog.reply;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReplyRepository extends MongoRepository<Reply, ObjectId>, CustomReplyRepository {

    boolean existsByExternalReplyId(String externalReplyId);

    Page<Reply> findByCommentIdAndBlogIdOrderByCreatedAtAsc(ObjectId commentId, ObjectId blogId, Pageable pageable);

    Optional<Reply> findByExternalReplyIdAndCommentIdAndBlogId(String externalReplyId, ObjectId commentId,
                                                               ObjectId blogId);
}
