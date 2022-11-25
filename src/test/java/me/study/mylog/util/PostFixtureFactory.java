package me.study.mylog.util;

import me.study.mylog.post.domain.Post;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.LocalDateRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.function.Predicate;

import static org.jeasy.random.FieldPredicates.*;

public class PostFixtureFactory {

    public static Post create() {
        EasyRandomParameters parameter = getEasyRandomParameters();
        return new EasyRandom(parameter).nextObject(Post.class);
    }

    private static EasyRandomParameters getEasyRandomParameters() {
        return new EasyRandomParameters()
                .excludeField(named("id"))
                .stringLengthRange(1, 100)
                .randomize(Long.class, new LongRangeRandomizer(1L, 100000L));
    }

    public static EasyRandom get(Long memberId) { //, LocalDateTime start, LocalDateTime end) {
        EasyRandomParameters parameter = getEasyRandomParameters();
        parameter
                .randomize(memberId(), () -> memberId);
                //.randomize(createdDate(), new LocalDateRangeRandomizer(start, end));

        return new EasyRandom(parameter);
    }

    private static Predicate<Field> memberId() {
        return named("userId").and(ofType(Long.class)).and(inClass(Post.class));
    }

    private static Predicate<Field> createdDate() {
        return named("createdDate").and(ofType(LocalDateTime.class)).and(inClass(Post.class));
    }
}
