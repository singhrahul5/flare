package dev.some.flare.blog.post;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPostRepository {
    List<Post> getTrendingPosts(Pageable pageable);
}
