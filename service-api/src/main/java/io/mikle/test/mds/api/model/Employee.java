package io.mikle.test.mds.api.model;

public record Employee(Integer id, String name, Department department) implements Entity {
    public Employee(String name, Department department) {
        this(null, name, department);
    }
}
