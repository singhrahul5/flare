package dev.some.flare.blog;

import dev.some.flare.blog.dto.CreateBlogRequest;
import dev.some.flare.blog.dto.BlogResponse;
import dev.some.flare.exception.NotFoundException;
import dev.some.flare.user.User;
import dev.some.flare.user.UserService;
import dev.some.flare.utils.RandomIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserService userService;
    private final RandomIdGeneratorService randomIdGeneratorService;

    @Transactional
    public void createBlog(CreateBlogRequest createBlogRequest, String username) {
        User user = userService.loadUserByUsername(username);

        String uniqueRandomId;
        int retries = 0;
        do {
            uniqueRandomId = randomIdGeneratorService.generateRandomId();
            try {
                if (!blogRepository.existsByExternalBlogId(uniqueRandomId)) {
                    Blog newBlog = Blog.builder()
                            .externalBlogId(uniqueRandomId)
                            .authorId(user.getId())
                            .content(createBlogRequest.getContent())
                            .createdAt(Instant.now())
                            .likeCount(0L)
                            .commentCount(0L)
                            .build();
                    blogRepository.save(newBlog);
                    return;
                }
            } catch (OptimisticLockingFailureException e) {
                retries++;
                if (retries >= 3) // retry limit
                    throw e;
            }
        } while (true);
    }

    private BlogResponse convertToBlogResponse(Blog blog) {
        String authorId = userService.getUserByUserId(blog.getAuthorId()).getUsername();
        return BlogResponse.builder()
                .blogId(blog.getExternalBlogId())
                .authorId(authorId)
                .content(blog.getContent())
                .createdAt(blog.getCreatedAt())
                .likeCount(blog.getLikeCount())
                .commentCount(blog.getCommentCount())
                .build();
    }

    public BlogResponse getBlogByExternalBlogId(String externalBlogId) {
        Blog blog = blogRepository.findByExternalBlogId(externalBlogId)
                .orElseThrow(() -> new NotFoundException("Blog not found."));

        return convertToBlogResponse(blog);
    }

    public List<BlogResponse> findBlogsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return blogRepository.getTrendingBlogs(pageable)
                .stream()
                .map(this::convertToBlogResponse)
                .toList();
    }

    public List<BlogResponse> findBlogsByUsernameWithPagination(String username, int page, int size) {
        User user = userService.loadUserByUsername(username);
        Pageable pageable = PageRequest.of(page-1, size);
        return blogRepository.findByAuthorIdOrderByCreatedAtDesc(user.getId(), pageable)
                .stream()
                .map(this::convertToBlogResponse)
                .toList();
    }
}
