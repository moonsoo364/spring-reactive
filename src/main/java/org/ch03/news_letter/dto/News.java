package org.ch03.news_letter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @JsonIgnore
    private ObjectId id;

    private @NotNull String title;
    private @NotNull String content;
    private @NotNull Date publishedOn;
    private @NotNull String category;
    private @NotNull String author;
}
