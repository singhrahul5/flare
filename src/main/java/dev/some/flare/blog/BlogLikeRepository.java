package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogLikeRepository extends MongoRepository<BlogLike, ObjectId> {
}
