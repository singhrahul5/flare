package dev.some.flare.blog.reply;

import dev.some.flare.blog.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomReplyRepositoryImpl implements CustomReplyRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void incrementLikeCount(ObjectId id) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)),
                new Update().inc("likeCount", 1),
                Reply.class);
    }

    @Override
    public void decrementLikeCount(ObjectId id) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)),
                new Update().inc("likeCount", -1),
                Reply.class);
    }
}
