package cn.leansd.base.model;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class GenericId {
    private String value;

    public GenericId(String value) {
        this.value = Objects.requireNonNull(value);
    }
    public String getValue() {
        return value;
    }

    /** 适用于所有Id对象的通用builder */
    public static GenericId of(Class<? extends GenericId> type, String value) {
        try {
            Constructor<? extends GenericId> constructor = type.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericId genericId = (GenericId) o;
        return Objects.equals(value, genericId.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    /**
     * Note： 因为我们希望使用@Embeddable， 所以这里必须提供一个无参数的构造函数
     * */
    public GenericId(){}

}