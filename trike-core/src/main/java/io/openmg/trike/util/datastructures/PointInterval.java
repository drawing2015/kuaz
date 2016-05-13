package io.openmg.trike.util.datastructures;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class PointInterval<T> implements Interval<T> {

    private final Set<T> points;

    private PointInterval(Set<T> points) {
        this.points = points;
    }

    public PointInterval(T point) {
        points=new HashSet<T>(1);
        points.add(point);
    }

    public PointInterval(T... points) {
        this(Arrays.asList(points));
    }

    public PointInterval(Iterable<T> points) {
        this.points=new HashSet<T>(4);
        Iterables.addAll(this.points,points);
    }

    @Override
    public Collection<T> getPoints() {
        return points;
    }

    public void setPoint(T point) {
        points.clear();
        points.add(point);
    }

    public void addPoint(T point) {
        points.add(point);
    }

    @Override
    public T getStart() {
        Preconditions.checkArgument(!isEmpty(),"There are no points in this interval");
        return (T)Collections.min(points,ComparableComparator.getInstance());
    }

    @Override
    public T getEnd() {
        Preconditions.checkArgument(!isEmpty(), "There are no points in this interval");
        return (T)Collections.max(points,ComparableComparator.getInstance());
    }

    @Override
    public boolean startInclusive() {
        return true;
    }

    @Override
    public boolean endInclusive() {
        return true;
    }

    @Override
    public boolean isPoints() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return points.isEmpty();
    }

    @Override
    public Interval<T> intersect(Interval<T> other) {
        Preconditions.checkArgument(other!=null);
        if (other instanceof PointInterval) {
            Set<T> merge = Sets.newHashSet(points);
            points.retainAll(((PointInterval)other).points);
            return new PointInterval<T>(points);
        } else if (other instanceof RangeInterval) {
            final RangeInterval<T> rint = (RangeInterval)other;
            return new PointInterval<T>(Sets.newHashSet(Iterables.filter(points,
                    new Predicate<T>() {
                        @Override
                        public boolean apply(@Nullable T t) {
                            return rint.containsPoint(t);
                        }
                    })));
        } else throw new AssertionError("Unexpected interval: " + other);
    }

    @Override
    public int hashCode() {
        return points.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this==other) return true;
        else if (other==null) return false;
        else if (!getClass().isInstance(other)) return false;
        PointInterval oth = (PointInterval)other;
        return points.equals(oth.points);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[");
        int i = 0;
        for (T point : points) {
            if (i>0) s.append(",");
            s.append(point);
            i++;
        }
        s.append("]");
        return s.toString();
    }
}
