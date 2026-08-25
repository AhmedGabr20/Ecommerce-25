package com.gabr.ecommerce.controllers;


import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.UserListResponse;
import com.gabr.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Users", description = "Users management APIs")
@RequestMapping("/api/admin/users")
@RestController
@RequiredArgsConstructor
public class UserController {

 private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserListResponse>> getUsers(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {

        return ApiResponse.success(
                "Users fetched successfully",
                userService.getUsers(pageable)
        );
    }

}
