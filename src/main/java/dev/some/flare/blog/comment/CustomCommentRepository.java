package dev.some.flare.blog.comment;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCommentRepository {
    List<Comment> getPopuralComments(ObjectId blogId, Pageable pageable);

    void incrementLikeCount(ObjectId id);

    void decrementLikeCount(ObjectId id);

    void incrementReplyCount(ObjectId id);
}
