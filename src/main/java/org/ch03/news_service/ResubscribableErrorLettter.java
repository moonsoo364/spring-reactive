package org.ch03.news_service;

import org.ch03.news_letter.dto.NewsLetter;
import org.reactivestreams.Subscriber;

public interface ResubscribableErrorLettter {
    void resubscribe(Subscriber<? super NewsLetter> subscriber);
}
