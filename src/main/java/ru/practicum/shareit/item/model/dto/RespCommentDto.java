package ru.practicum.shareit.item.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class RespCommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
