package cn.leansd.base.model;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class GenericId {
    private String id;

    public GenericId(String id) {
        this.id = Objects.requireNonNull(id);
    }
    public String getId() {
        return id;
    }

    /** 适用于所有Id对象的通用builder */
    public static GenericId of(Class<? extends GenericId> type, String id) {
        try {
            Constructor<? extends GenericId> constructor = type.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericId genericId = (GenericId) o;
        return Objects.equals(id, genericId.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Note： 因为我们希望使用@Embeddable， 所以这里必须提供一个无参数的构造函数
     * */
    public GenericId(){}

}