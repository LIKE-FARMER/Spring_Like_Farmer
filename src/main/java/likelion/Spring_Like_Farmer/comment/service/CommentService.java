package likelion.Spring_Like_Farmer.comment.service;

import likelion.Spring_Like_Farmer.comment.domain.Comment;
import likelion.Spring_Like_Farmer.comment.dto.CommentDto;
import likelion.Spring_Like_Farmer.comment.repository.CommentRepository;
import likelion.Spring_Like_Farmer.post.domain.Post;
import likelion.Spring_Like_Farmer.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long createComment(CommentDto.CreateComment request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + request.getPostId()));
        Comment comment = new Comment(request.getNickname(), request.getPassword(), request.getContent(), post);
        commentRepository.save(comment);
        return comment.getCommentId();
    }

    @Transactional
    public void updateComment(Long commentId, CommentDto.UpdateComment request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));
        comment.setContent(request.getContent());
    }
}
