package likelion.Spring_Like_Farmer.user.service;

import likelion.Spring_Like_Farmer.security.TokenProvider;
import likelion.Spring_Like_Farmer.security.UserPrincipal;
import likelion.Spring_Like_Farmer.user.domain.User;
import likelion.Spring_Like_Farmer.user.dto.TokenDto;
import likelion.Spring_Like_Farmer.user.dto.UserDto;
import likelion.Spring_Like_Farmer.user.repository.UserRepository;
import likelion.Spring_Like_Farmer.validation.CustomException;
import likelion.Spring_Like_Farmer.validation.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public TokenDto createToken(Authentication authentication, Long userId) {

        String accessToken = tokenProvider.createToken(authentication, Boolean.FALSE); // access
        String refreshToken = tokenProvider.createToken(authentication, Boolean.TRUE); // refresh

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Object signup(UserDto.SignupUser signupUser) {

        Optional<User> findUser = userRepository.findById(signupUser.getId());
        if (findUser.isPresent()) {
            return new UserDto.DuplicateUserResponse(ExceptionCode.SIGNUP_DUPLICATED_ID); // ID 중복
        }

        findUser = userRepository.findByNickname(signupUser.getNickname());
        if (findUser.isPresent()) {
            return new UserDto.DuplicateUserResponse(ExceptionCode.SIGNUP_DUPLICATED_NICKNAME); // NICKNAME 중복
        }

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = new User(signupUser, encoder.encode(signupUser.getPw()));

        userRepository.save(user);

        return new UserDto.UserResponse(ExceptionCode.SIGNUP_CREATED_OK);
    }

    public Object login(UserDto.LoginUser loginUser) {

        Optional<User> findUser = userRepository.findById(loginUser.getId());

        if (findUser.isEmpty()) {
            return new UserDto.DuplicateUserResponse(ExceptionCode.LOGIN_NOT_FOUND_ID);
        } else if (! PasswordEncoderFactories.createDelegatingPasswordEncoder().matches(loginUser.getPw(), findUser.get().getPw())) {
            return new UserDto.DuplicateUserResponse(ExceptionCode.LOGIN_NOT_FOUND_PW);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        findUser.get().getId(),
                        loginUser.getPw()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = createToken(authentication, findUser.get().getUserId());
        findUser.get().setToken(tokenDto.getRefreshToken());
        userRepository.save(findUser.get());

        return new UserDto.LoginResponse(ExceptionCode.LOGIN_OK, findUser.get(), tokenDto);
    }

    public Object updateNickname(UserPrincipal userPrincipal, UserDto.UpdateUser updateUser) {

        User user = userRepository.findByUserId(userPrincipal.getUserId())
                .orElseThrow(
                        () -> {
                            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                        });

        // description, location update

        return new UserDto.UserResponse(ExceptionCode.USER_UPDATE_OK);
    }

    public Object findUser(UserPrincipal userPrincipal, Long userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(
                        () -> {
                            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                        });

        return new UserDto.UserInfoResponse(ExceptionCode.USER_GET_OK, user);
    }

}
