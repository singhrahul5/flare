package dev.some.flare.blog;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomBlogRepository {
    List<Blog> getTrendingBlogs(Pageable pageable);

    void incrementCommentCount(ObjectId id);
}
