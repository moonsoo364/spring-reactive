package chapter_05.reactive;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveTypeDescriptor;

import java.util.function.Function;

public class MaybeReactiveAdapter extends ReactiveAdapter {
    public MaybeReactiveAdapter(){
        super(
                ReactiveTypeDescriptor.singleOptionalValue(Maybe.class,Maybe::empty),
                rawMaybe -> ((Maybe<?>) rawMaybe).toFlowable(),
                publisher -> Flowable.fromPublisher(publisher)
                        .singleElement()
        );
    }
}
