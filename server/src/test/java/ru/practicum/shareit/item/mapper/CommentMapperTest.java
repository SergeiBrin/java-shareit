package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private final User user = new User();
    private final User author = new User();
    private final Item item = new Item();
    private final ItemRequest request = new ItemRequest();
    private final Comment comment = new Comment();
    private final String textComment = "Text comment";

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        author.setId(2L);
        author.setName("Author");
        author.setEmail("author@gmail.com");

        request.setId(1L);
        request.setCreator(new User());
        request.setDescription("Text description");
        request.setCreated(LocalDateTime.now());

        item.setId(1L);
        item.setUser(user);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequest(request);

        comment.setId(1L);
        comment.setText(textComment);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void buildComment_ShouldReturnComment() {
        Comment buildComment = CommentMapper.buildComment(textComment, item, author);

        assertThat(buildComment.getText(), equalTo(textComment));
        assertThat(buildComment.getItem(), equalTo(item));
        assertThat(buildComment.getAuthor(), equalTo(author));
        assertThat(buildComment.getCreated(), is(notNullValue()));
    }

    @Test
    void buildCommentDto_ShouldReturnRespCommentDto() {
        RespCommentDto buildCommentDto = CommentMapper.buildCommentDto(comment);

        assertThat(buildCommentDto.getId(), equalTo(comment.getId()));
        assertThat(buildCommentDto.getText(), equalTo(comment.getText()));
        assertThat(buildCommentDto.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertEquals(comment.getCreated(), buildCommentDto.getCreated());
    }

    @Test
    void buildCommentDtoList_ShouldReturnRespCommentDtoList() {
        List<RespCommentDto> buildCommentDtos = CommentMapper.buildCommentDtoList(List.of(comment));
        assertThat(buildCommentDtos, hasSize(1));

        RespCommentDto buildCommentDto = buildCommentDtos.get(0);

        assertThat(buildCommentDto.getId(), equalTo(comment.getId()));
        assertThat(buildCommentDto.getText(), equalTo(comment.getText()));
        assertThat(buildCommentDto.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertEquals(comment.getCreated(), buildCommentDto.getCreated());
    }
}