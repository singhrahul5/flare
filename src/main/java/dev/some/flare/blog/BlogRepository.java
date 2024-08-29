package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlogRepository extends MongoRepository<Blog, ObjectId>, CustomBlogRepository {
    boolean existsByExternalBlogId(String externalBlogId);

    Optional<Blog> findByExternalBlogId(String externalBlogId);

    Page<Blog> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

}
