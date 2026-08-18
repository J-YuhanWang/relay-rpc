package dev.yuhanwang.relayrpc.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    //serialVersionUID represents the version ID of the Serializable class,
    // Without this field, the JVM computes a hash usting SHA-1 based on class details.
    // which changes whenever the class structure is modified
    // Setting it explicitly to 1L ensures continuous compatibility across different version
    private static final Long serialVersionUID=1L;
    private Long id;
    private String name;

}
