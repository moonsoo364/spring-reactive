package org.example.ch03.news_letter.dto;

import lombok.*;
import lombok.experimental.Wither;

import java.util.Collection;

@Data(staticConstructor = "of")
@Builder(builderClassName = "NewsLetterTemplate", builderMethodName = "template")
@AllArgsConstructor
@Wither
@NoArgsConstructor(force = true)
public class NewsLetter {
    private final @NonNull String title;
    private final String recipient;
    private final @NonNull Collection<News> digest;
}

