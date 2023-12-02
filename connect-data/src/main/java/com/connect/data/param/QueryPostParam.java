package com.connect.data.param;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class QueryPostParam {
    private Long postId;

    private String userId;

    private String keyword;
}