package likelion.Spring_Like_Farmer.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenDto {

    private String accessToken;
    private String refreshToken;
}
