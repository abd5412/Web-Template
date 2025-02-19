package com.abd.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("dub_authority")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authority implements Serializable {

    private Long id;
    private String authorityName;
    private String label;
}
