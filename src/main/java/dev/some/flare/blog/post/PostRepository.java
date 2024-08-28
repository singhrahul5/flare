package dev.some.flare.blog.post;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, ObjectId>, CustomPostRepository {
    boolean existsByExternalPostId(String externalPostId);

    Optional<Post> findByExternalPostId(String externalPostId);

    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

}
