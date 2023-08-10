package likelion.Spring_Like_Farmer.post.domain;

import jakarta.persistence.*;
import likelion.Spring_Like_Farmer.config.BaseEntity;
import likelion.Spring_Like_Farmer.item.dto.ItemDto;
import likelion.Spring_Like_Farmer.post.dto.PostDto;
import likelion.Spring_Like_Farmer.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "user_image")
    private String userImage;
    @Column(name = "user_nickname")
    private String userNickname;

    @Column(name = "user_location")
    private String userLocation;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Builder
    public Post(User user, PostDto.SavePost savePost) {
        this.user = user;
        this.userImage = user.getImage();
        this.userNickname = user.getNickname();
        this.userLocation = savePost.getLocation();
        this.image = savePost.getImage();
        this.description = savePost.getDescription();
        this.createdDate = savePost.getCreatedDate();
    }
    public void updatePost(PostDto.SavePost savePost) {
        this.userLocation = savePost.getLocation();
        this.image = savePost.getImage();
        this.description = savePost.getDescription();
        this.createdDate = savePost.getCreatedDate();
    }
}
