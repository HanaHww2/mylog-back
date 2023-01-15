package me.study.mylog.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.oauth2.sdk.ResponseType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class ApiResponse<T> {
//    private ResponseType type;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    T result;

    public ApiResponse(String message, @Nullable T result) {
        this.message = message;
        this.result = result;
    }
}
