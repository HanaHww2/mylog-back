package me.study.mylog.users.dto;


import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
public class UserValidationDto {
    @Email
    @Size(min = 5, max = 100)
    String email;

    @Size(min = 1, max = 100)
    String name;
}
