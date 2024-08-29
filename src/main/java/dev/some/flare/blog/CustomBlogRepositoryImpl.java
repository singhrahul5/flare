package dev.some.flare.blog;

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
public class CustomBlogRepositoryImpl implements CustomBlogRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Blog> getTrendingBlogs(Pageable pageable) {

        // Step 1: Calculate timeDifference in hours
        AggregationOperation addTimeDifference = Aggregation.addFields()
                .addField("timeDifference")
                .withValue(
                        ArithmeticOperators.Divide.valueOf(
                                ArithmeticOperators.Subtract.valueOf("$$NOW").subtract("createdAt")
                        ).divideBy(1000 * 60 * 60)
                ).build();

        // Step 2: Calculate timeDecay using $cond
        ConditionalOperators.Cond timeDecayCondition = ConditionalOperators.when(Criteria.where("timeDifference").gt(0))
                .thenValueOf(
                        ArithmeticOperators.Divide.valueOf(1).divideBy(
                                ArithmeticOperators.Add.valueOf(1).add("timeDifference")
                        )
                )
                .otherwise(1);

        AggregationOperation addTimeDecay = Aggregation.addFields()
                .addField("timeDecay")
                .withValue(timeDecayCondition)
                .build();

        // Step 3: Calculate composite score
        AggregationOperation addScore = Aggregation.addFields()
                .addField("score")
                .withValue(
                        ArithmeticOperators.Add.valueOf(
                                ArithmeticOperators.Multiply.valueOf(1.0).multiplyBy("likeCount")
                        ).add(
                                ArithmeticOperators.Multiply.valueOf(1.5).multiplyBy("commentCount")
                        ).add(
                                ArithmeticOperators.Multiply.valueOf(2.0).multiplyBy("timeDecay")
                        )
                ).build();

        // Step 4: Sort by score
        AggregationOperation sortByScore = Aggregation.sort(Sort.by("score").descending());

        // Step 5: Pagination: Skip and Limit
        long offset = pageable.getOffset();
        int size = pageable.getPageSize();
        AggregationOperation skip = Aggregation.skip(offset);
        AggregationOperation limit = Aggregation.limit(size);

        // Step 6: Build the aggregation pipeline with pagination

        TypedAggregation<Blog> typedAggregation = TypedAggregation.newAggregation(
                Blog.class, addTimeDifference, addTimeDecay, addScore, sortByScore, skip, limit
        );

        // Step 7: Execute the aggregation query
        AggregationResults<Blog> results = mongoTemplate.aggregate(typedAggregation, Blog.class);
        return results.getMappedResults();
    }

    @Override
    public void incrementCommentCount(ObjectId id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().inc("commentCount", 1);
        mongoTemplate.updateFirst(query, update, Blog.class);
    }

    @Override
    public void incrementLikeCount(ObjectId id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().inc("likeCount", 1);
        mongoTemplate.updateFirst(query, update, Blog.class);
    }

    @Override
    public void decrementLikeCount(ObjectId id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().inc("likeCount", -1);
        mongoTemplate.updateFirst(query, update, Blog.class);
    }
}
