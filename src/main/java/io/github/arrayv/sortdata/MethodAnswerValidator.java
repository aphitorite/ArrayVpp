package io.github.arrayv.sortdata;

import io.github.arrayv.sorts.templates.Sort;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.UnaryOperator;

public final class MethodAnswerValidator implements UnaryOperator<Integer> {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();
    private static final MethodType VALIDATOR_TYPE = MethodType.methodType(int.class, int.class);

    private final MethodHandle mh;

    public MethodAnswerValidator(MethodHandle mh) {
        this.mh = mh;
    }

    public MethodAnswerValidator(Method validator) throws IllegalAccessException {
        this(LOOKUP.unreflect(validator));
    }

    public MethodAnswerValidator(Class<? extends Sort> sortClass) throws NoSuchMethodException, IllegalAccessException {
        this(getMh(sortClass));
    }

    public MethodAnswerValidator(Sort sort) throws IllegalAccessException, NoSuchMethodException {
        this(LOOKUP.findVirtual(sort.getClass(), "validateAnswer", VALIDATOR_TYPE).bindTo(sort));
    }

    private static MethodHandle getMh(Class<? extends Sort> sortClass) throws NoSuchMethodException, IllegalAccessException {
        return LOOKUP.findVirtual(sortClass, "validateAnswer", VALIDATOR_TYPE);
    }

    public MethodHandle getValidatorHandle() {
        return mh;
    }

    public Integer apply(Integer answer) {
        try {
            return (int)mh.invokeExact(answer);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
