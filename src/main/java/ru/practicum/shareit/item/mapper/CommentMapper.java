package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment buildComment(String text, Item item, User author) {
        return Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .build();
    }

    public static List<RespCommentDto> buildCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::buildCommentDto)
                .collect(Collectors.toList());
    }

    public static RespCommentDto buildCommentDto(Comment comment) {
        return RespCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
