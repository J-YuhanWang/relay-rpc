package dev.yuhanwang.relayrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RpcResponse implements Serializable {
    private static final Long serialVersionUID=1L;
    //data, message, datatype, exception
    private Object data;
    private Class<?> dataType;
    private String message;
    private Exception exception;
}
