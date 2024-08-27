package dev.some.flare.blog.post;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, ObjectId> {
    boolean existsByExternalPostId(String externalPostId);

    Optional<Post> findByExternalPostId(String externalPostId);
}
