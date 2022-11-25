package org.apereo.cas.util.spring.beans;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This is {@link BeanContainer}.
 *
 * @author Misagh Moayyed
 * @since 6.5.0
 */
public interface BeanContainer<T> {

    /**
     * To list container bean.
     *
     * @param <T>     the type parameter
     * @param entries the entries
     * @return the container bean
     */
    static <T> BeanContainer<T> of(final T... entries) {
        return new ListBeanContainer<>(Arrays.stream(entries).collect(Collectors.toList()));
    }

    /**
     * Contain items in a list..
     *
     * @param <T>     the type parameter
     * @param entries the entries
     * @return the bean container
     */
    static <T> BeanContainer<T> of(final List<? extends T> entries) {
        return new ListBeanContainer<>(new ArrayList<>(entries));
    }

    /**
     * Of bean container.
     *
     * @param <T>     the type parameter
     * @param entries the entries
     * @return the bean container
     */
    static <T> BeanContainer<T> of(final Set<? extends T> entries) {
        return new ListBeanContainer<>(new ArrayList<>(entries));
    }

    /**
     * Empty bean container.
     *
     * @param <T> the type parameter
     * @return the bean container
     */
    static <T> BeanContainer<T> empty() {
        return BeanContainer.of();
    }

    /**
     * Gets items.
     *
     * @return the items
     */
    List<T> toList();

    /**
     * To set set.
     *
     * @return the set
     */
    Set<T> toSet();

    /**
     * And include a single item..
     *
     * @param entry the entry
     * @return the container bean
     */
    BeanContainer<T> and(T entry);

    /**
     * Size.
     *
     * @return the int
     */
    int size();

    /**
     * First entry in the container.
     *
     * @return the entry
     */
    T first();

    /**
     * Is container empty?
     *
     * @return true/false
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * For each iterator.
     *
     * @param o the o
     * @return the bean container
     */
    BeanContainer<T> forEach(Consumer<T> o);

    @RequiredArgsConstructor
    class ListBeanContainer<T> implements BeanContainer<T> {
        private final List<T> items;

        @Override
        public List<T> toList() {
            return this.items;
        }

        @Override
        public Set<T> toSet() {
            return new LinkedHashSet<>(this.items);
        }

        @Override
        @CanIgnoreReturnValue
        public BeanContainer<T> and(final T entry) {
            items.add(entry);
            return this;
        }

        @Override
        public int size() {
            return this.items.size();
        }

        @Override
        public T first() {
            return items.get(0);
        }

        @Override
        public BeanContainer<T> forEach(final Consumer<T> o) {
            items.forEach(o);
            return null;
        }
    }
}
