package org.ch03.news_letter;

import org.ch03.news_letter.dto.News;

import java.util.Date;
import java.util.Random;

public interface NewsHarness {
    Random RANDOM = new Random();

    static News generate() {
        return News.builder()
                .author(String.valueOf(RANDOM.nextGaussian()))
                .category("tech")
                .publishedOn(new Date())
                .content(String.valueOf(RANDOM.nextGaussian()))
                .title(String.valueOf(RANDOM.nextGaussian()))
                .build();
    }
}
