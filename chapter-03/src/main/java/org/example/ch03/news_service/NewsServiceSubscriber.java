package org.example.ch03.news_service;

import org.example.ch03.news_letter.dto.NewsLetter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsServiceSubscriber implements Subscriber<NewsLetter> {//1

    final Queue<NewsLetter> mailbox = new ConcurrentLinkedQueue <>();
    final AtomicInteger remaining = new AtomicInteger();
    final int take;

    Subscription subscription;

    public NewsServiceSubscriber(int take){//2
        this.take = take;
        this.remaining.set(take);
    }


    @Override
    public void onSubscribe(Subscription s) {//3
        if(subscription == null) {
            subscription = s;
            subscription.request(take);
        } else {
            s.cancel();
        }
    }

    @Override
    public void onNext(NewsLetter newsLetter) {//4
        Objects.requireNonNull(newsLetter);
        mailbox.offer(newsLetter);
    }

    @Override
    public void onError(Throwable t) {//5
        Objects.requireNonNull(t);

        if(t instanceof ResubscribableErrorLettter) {
            subscription = null;
            ((ResubscribableErrorLettter) t).resubscribe(this);
        }
    }

    @Override
    public void onComplete() {
        subscription = null;
    }

    public Optional<NewsLetter> eventuallyReadDigest() {//6
        NewsLetter letter = mailbox.poll();//6.1
        if(letter != null) {
            if(remaining.decrementAndGet() == 0) {//6.2
                subscription.request(take);
                remaining.set(take);
            }
            return Optional.of(letter);//6.3
        }
        return Optional.empty();//6.4
    }
}
