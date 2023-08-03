package likelion.Spring_Like_Farmer.post.service;

import likelion.Spring_Like_Farmer.post.domain.Post;
import likelion.Spring_Like_Farmer.post.dto.PostDto;
import likelion.Spring_Like_Farmer.post.repository.PostRepository;
import likelion.Spring_Like_Farmer.user.domain.User;
import likelion.Spring_Like_Farmer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(PostDto.CreatePost request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
        Post post = Post.builder()
                .user(user)
                .userImage(request.getUserImage())
                .userNickname(request.getUserNickname())
                .userLocation(request.getUserLocation())
                .image(request.getImage())
                .description(request.getDescription())
                .comment(request.getComment())
                .build();
        postRepository.save(post);
        return post.getPostId();
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        postRepository.delete(post);
    }
}
