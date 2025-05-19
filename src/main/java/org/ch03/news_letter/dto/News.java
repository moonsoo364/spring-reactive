package org.ch03.news_letter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @JsonIgnore
    private ObjectId id;

    private @NotNull String title;
    private @NotNull String content;
    private @NotNull String publishedOn;
    private @NotNull String category;
    private @NotNull String author;
}
