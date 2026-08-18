package com.llmmentor.springai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

public record Book(@JsonPropertyDescription("书名") String name,
                   @JsonPropertyDescription("作者") String author,
                   @JsonPropertyDescription("描述") String desc,
                   @JsonPropertyDescription("价格") BigDecimal price,
                   @JsonPropertyDescription("出版社") String publisher) {
}
