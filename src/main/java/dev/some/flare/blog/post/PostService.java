package dev.some.flare.blog.post;

import dev.some.flare.blog.post.dto.CreatePostRequest;
import dev.some.flare.blog.post.dto.PostResponse;
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
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final RandomIdGeneratorService randomIdGeneratorService;

    @Transactional
    public void createPost(CreatePostRequest createPostRequest, String username) {
        User user = userService.loadUserByUsername(username);

        String uniqueRandomId;
        int retries = 0;
        do {
            uniqueRandomId = randomIdGeneratorService.generateRandomId();
            try {
                if (!postRepository.existsByExternalPostId(uniqueRandomId)) {
                    Post newPost = Post.builder()
                            .externalPostId(uniqueRandomId)
                            .authorId(user.getId())
                            .content(createPostRequest.getContent())
                            .createdAt(Instant.now())
                            .likeCount(0L)
                            .commentCount(0L)
                            .build();
                    postRepository.save(newPost);
                    return;
                }
            } catch (OptimisticLockingFailureException e) {
                retries++;
                if (retries >= 3) // retry limit
                    throw e;
            }
        } while (true);
    }

    private PostResponse convertToPostResponse(Post post) {
        String authorId = userService.getUserByUserId(post.getAuthorId()).getUsername();
        return PostResponse.builder()
                .postId(post.getExternalPostId())
                .authorId(authorId)
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();
    }

    public PostResponse getPostByExternalPostId(String externalPostId) {
        Post post = postRepository.findByExternalPostId(externalPostId)
                .orElseThrow(() -> new NotFoundException("Blog not found."));

        return convertToPostResponse(post);
    }

    public List<PostResponse> findPostsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return postRepository.getTrendingPosts(pageable)
                .stream()
                .map(this::convertToPostResponse)
                .toList();
    }

    public List<PostResponse> findPostsByUsernameWithPagination(String username, int page, int size) {
        User user = userService.loadUserByUsername(username);
        Pageable pageable = PageRequest.of(page-1, size);
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(user.getId(), pageable)
                .stream()
                .map(this::convertToPostResponse)
                .toList();
    }
}
