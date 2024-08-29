package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlogLikeRepository extends MongoRepository<BlogLike, ObjectId> {

    Optional<BlogLike> findByBlogIdAndUserId(ObjectId blogId, Long userId);

}
