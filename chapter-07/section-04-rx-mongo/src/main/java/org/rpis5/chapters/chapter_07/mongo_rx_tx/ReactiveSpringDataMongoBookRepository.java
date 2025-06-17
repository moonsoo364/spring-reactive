package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import org.springframework.data.mongodb.repository.Meta;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReactiveSpringDataMongoBookRepository extends ReactiveMongoRepository<Book, Integer> {
    @Meta(maxScanDocuments = 3)
    Flux<Book> findByAuthorOrderByPublishingYearDesc(
            Flux<String> authors
    );

    @Query("{ 'authores.1 : { $exists: true} }")
    Flux<Book> booksWithFewAuthors();
}
