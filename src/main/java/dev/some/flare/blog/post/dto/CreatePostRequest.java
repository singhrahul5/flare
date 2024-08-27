package dev.some.flare.blog.post.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @Size(min = 1, max = 1000, message = "blog post content size should be between 1 - 1000.")
    private String content;
}
