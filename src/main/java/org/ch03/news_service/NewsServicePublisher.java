package org.ch03.news_service;

import com.mongodb.reactivestreams.client.MongoClient;
import org.ch03.news_letter.dto.News;
import org.ch03.news_letter.dto.NewsLetter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NewsServicePublisher implements Publisher<NewsLetter> {

	final SmartMulticastProcessor processor;

	public NewsServicePublisher(MongoClient client, String categoryOfInterests) {
		ScheduledPublisher<NewsLetter> scheduler = new ScheduledPublisher<>(
				() -> new NewsPreparationOperator(
						new DBPublisher(
								client.getDatabase("news")
								      .getCollection("news", News.class),
								categoryOfInterests
						),
						"Some Digest"
				),
				1, TimeUnit.DAYS
		);

		SmartMulticastProcessor processor = new SmartMulticastProcessor();
		scheduler.subscribe(processor);

		this.processor = processor;
	}

	public NewsServicePublisher(Consumer<SmartMulticastProcessor> setup) {
		this.processor = new SmartMulticastProcessor();

		setup.accept(processor);
	}

	@Override
	public void subscribe(Subscriber<? super NewsLetter> s) {
		processor.subscribe(s);
	}
}
