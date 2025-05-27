package org.example.ch03.news_service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.example.ch03.news_letter.dto.News;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Date;

public class DBPublisher implements Publisher<News> {
    private final MongoCollection<News> collection;
    private final String category;

    public DBPublisher(MongoCollection<News> collection, String category) {
        this.collection = collection;
	    this.category = category;
    }

    @Override
    public void subscribe(Subscriber<? super News> s) {
	    FindPublisher<News> findPublisher = collection.find(News.class);
	    findPublisher.sort(Sorts.descending("publishedOn"))
			     .filter(Filters.and(
		            Filters.eq("category", category),
			        Filters.gt("publishedOn", today())
			     ))
	             .subscribe(s);
    }

    private Date today() {
	    Date date = new Date();
	    return new Date(date.getYear(), date.getMonth(), date.getDate());
    }
}
