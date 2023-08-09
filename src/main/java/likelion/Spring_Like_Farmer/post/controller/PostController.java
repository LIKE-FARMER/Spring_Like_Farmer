package likelion.Spring_Like_Farmer.post.controller;

import likelion.Spring_Like_Farmer.post.dto.PostDto;
import likelion.Spring_Like_Farmer.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 올리기
    @PostMapping("/create")
    public Object createPost(@RequestBody PostDto.CreatePost request) {
        return postService.createPost(request);
    }

    // 게시글 수정하기
    @PatchMapping("/update/{postId}")
    public Object updatePost(@PathVariable Long postId, @RequestBody PostDto.UpdatePost request) {
        return postService.updatePost(postId, request);
    }

    // 게시글 삭제하기
    @DeleteMapping("/delete/{postId}")
    public Object deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }
}
