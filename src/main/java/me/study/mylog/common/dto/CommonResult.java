package me.study.mylog.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class CommonResult<T> {
    //private ResponseType type;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    T data;

    public CommonResult(String message, @Nullable T data) {
        this.message = message;
        this.data = data;
    }
}
