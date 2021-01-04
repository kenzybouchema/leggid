package com.kb.leggid.controller;

import com.kb.leggid.dto.CommentsDto;
import com.kb.leggid.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentsDto> createComment(@RequestBody CommentsDto commentsDto) {
        CommentsDto createdCommentsDto = commentService.createComment(commentsDto);
        return status(CREATED).body(createdCommentsDto);
    }

    @GetMapping
    public ResponseEntity<List<CommentsDto>> getAllComments() {
        return status(OK).body(commentService.getAllComments());
    }

    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsbyPost(@PathVariable Long postId) {
        return status(OK).body(commentService.getCommentByPost(postId));
    }

    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByUser(@PathVariable String userName) {
        return status(OK).body(commentService.getCommentsByUser(userName));
    }
}
