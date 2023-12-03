package com.connect.api.user;

import com.connect.api.common.APIResponse;
import com.connect.api.user.request.*;
import com.connect.api.user.response.QueryUserResponse;
import com.connect.api.user.response.SignInResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RequestMapping(value = "/api/connect/v1")
public interface IUserApi {
    @PostMapping(value = {"/user/signin", "/public/user/signin"})
    APIResponse<SignInResponse> signIn(
            @Validated @RequestBody SignInRequest request
    );

    @PostMapping(value = {"/user/signup", "/public/user/signup"})
    APIResponse<Void> signUp(
            @Validated @RequestBody SignUpRequest request
    );

    @PatchMapping(value = "/user/me/edit")
    APIResponse<Void> editPersonalInfo(
            @RequestHeader Map<String, String> header,
            @Validated @RequestBody EditUserRequest request
    );

    @PatchMapping(value = "/user/{userId}")
    APIResponse<Void> editProfile(
            @RequestHeader Map<String, String> header,
            @Validated @NotNull @PathVariable String userId,
            @Validated @RequestBody EditProfileRequest request
    );

    @DeleteMapping(value = "/user/{userId}")
    APIResponse<Void> deleteUser(
            @RequestHeader Map<String, String> header,
            @Validated @NotNull @PathVariable String userId
    );

    @GetMapping(value = "/users/me")
    APIResponse<QueryUserResponse> queryPersonalInfo();

    @GetMapping(value = "/user/{userId}")
    APIResponse<QueryUserResponse> queryUser(@NotNull @PathVariable String userId);

    @GetMapping(value = "/user")
    APIResponse<QueryUserResponse> queryUserWithFilter(
            @Validated QueryUserRequest request
    );
}
