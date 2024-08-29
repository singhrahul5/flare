package dev.some.flare.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @Size(min = 1, max = 1000, message = "comment content size should be between 1 - 1000.")
    private String content;
}
