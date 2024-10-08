package dev.some.flare.blog.comment;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Comment> getPopuralComments(ObjectId blogId, Pageable pageable) {
        // filter comments by blogId
        AggregationOperation filterCommentsByBlogId = Aggregation.match(Criteria.where("blogId").is(blogId));
        // Calculate timeDifference in hours
        AggregationOperation addTimeDifference =
                Aggregation.addFields().addField("timeDifference").withValue(ArithmeticOperators.Divide.valueOf(ArithmeticOperators.Subtract.valueOf("$$NOW").subtract("createdAt")).divideBy(1000 * 60 * 60)).build();

        // Calculate timeDecay using $cond
        ConditionalOperators.Cond timeDecayCondition =
                ConditionalOperators.when(Criteria.where("timeDifference").gt(0)).thenValueOf(ArithmeticOperators.Divide.valueOf(1).divideBy(ArithmeticOperators.Add.valueOf(1).add("timeDifference"))).otherwise(1);

        AggregationOperation addTimeDecay =
                Aggregation.addFields().addField("timeDecay").withValue(timeDecayCondition).build();

        // Calculate composite score
        AggregationOperation addScore =
                Aggregation.addFields().addField("score").withValue(ArithmeticOperators.Add.valueOf(ArithmeticOperators.Multiply.valueOf(1.0).multiplyBy("likeCount")).add(ArithmeticOperators.Multiply.valueOf(1.5).multiplyBy("replyCount")).add(ArithmeticOperators.Multiply.valueOf(2.0).multiplyBy("timeDecay"))).build();

        // Sort by score
        AggregationOperation sortByScore = Aggregation.sort(Sort.by("score").descending());

        // Pagination: Skip and Limit
        long offset = pageable.getOffset();
        int size = pageable.getPageSize();
        AggregationOperation skip = Aggregation.skip(offset);
        AggregationOperation limit = Aggregation.limit(size);

        // Build the aggregation pipeline with pagination

        TypedAggregation<Comment> typedAggregation = TypedAggregation.newAggregation(Comment.class,
                filterCommentsByBlogId, addTimeDifference, addTimeDecay, addScore, sortByScore, skip, limit);

        // Execute the aggregation query
        AggregationResults<Comment> results = mongoTemplate.aggregate(typedAggregation, Comment.class);
        return results.getMappedResults();
    }

    @Override
    public void incrementLikeCount(ObjectId id) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)),
                new Update().inc("likeCount", 1),
                Comment.class);
    }

    @Override
    public void decrementLikeCount(ObjectId id) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)),
                new Update().inc("likeCount", -1),
                Comment.class);
    }

    @Override
    public void incrementReplyCount(ObjectId id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().inc("replyCount", 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Comment.class);
    }
}
