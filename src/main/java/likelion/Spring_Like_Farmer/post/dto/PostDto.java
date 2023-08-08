package likelion.Spring_Like_Farmer.post.dto;

import likelion.Spring_Like_Farmer.post.domain.Post;
import likelion.Spring_Like_Farmer.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.hibernate.sql.InFragment.NULL;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private Long userId;
    private String userImage;
    private String userNickname;
    private String userLocation;
    private String image;
    private String description;
    private String comment;

    @Getter
    @Setter
    public static class CreatePost {
        private Long userId;
        private String userImage;
        private String userNickname;
        private String userLocation;
        private String image;
        private String description;
        private String comment = NULL;

        @Builder
        public CreatePost(Long userId, String userImage, String userNickname,
                          String userLocation, String image, String description, String comment) {
            this.userId = userId;
            this.userImage = userImage;
            this.userNickname = userNickname;
            this.userLocation = userLocation;
            this.image = image;
            this.description = description;
            this.comment = comment;
        }

    }
    @Getter
    @Setter
    public static class UpdatePost {

        private String image;
        private String description;
        private String comment;
        @Builder
        public UpdatePost(String image, String description, String comment) {
            this.image = image;
            this.description = description;
            this.comment = comment;
        }
    }


    public PostDto(Post post) {
        this.postId = post.getPostId();
        User user = post.getUser();
        if (user != null) {
            this.userId = user.getUserId();
            this.userImage = post.getUserImage();
            this.userNickname = post.getUserNickname();
            this.userLocation = post.getUserLocation();
        }
        this.image = post.getImage();
        this.description = post.getDescription();
        this.comment = post.getComment();
    }
}
