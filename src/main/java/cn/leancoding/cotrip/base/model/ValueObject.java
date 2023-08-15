package cn.leancoding.cotrip.base.model;
public abstract class ValueObject {
    @Override
    public abstract boolean equals(Object o);
    @Override
    public abstract int hashCode();
}
