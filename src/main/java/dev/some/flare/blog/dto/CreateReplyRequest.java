package dev.some.flare.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReplyRequest {
    @Size(min = 1, max = 1000, message = "reply content size should be between 1 - 100.")
    private String content;
}
