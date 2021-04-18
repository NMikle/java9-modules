package io.mikle.test.mds.core.service;

import io.mikle.test.mds.api.model.Department;
import io.mikle.test.mds.api.model.Employee;
import io.mikle.test.mds.api.service.EmployeeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class ListEmployeeService implements EmployeeService {

    public static final Function<Employee, String> USER_TO_NAME = Employee::name;
    public static final Predicate<String> IS_NULL = Objects::isNull;

    private final List<Employee> employees;
    private int size;
    private final Lock lock;

    public ListEmployeeService() {
        this.employees = new CopyOnWriteArrayList<>();
        this.size = 0;
        this.lock = new ReentrantLock();
    }

    public ListEmployeeService(Collection<Employee> employees) {
        this.employees = new CopyOnWriteArrayList<>(employees);
        this.size = employees.size();
        this.lock = new ReentrantLock();
    }

    @Override
    public Employee save(Employee entity) {
        System.out.println("saving user");
        try {
            lock.lock();
            checkUserNameDuplicates(entity);
            return saveUserToCollection(entity);
        } finally {
            lock.unlock();
            System.out.println("user saved");
        }
    }

    @Override
    public Employee findById(Integer id) {
        return employees.get(id - 1);
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

    @Override
    public Employee delete(Integer id) {
        final Employee employee = employees.get(id - 1);
        employees.remove(employee);
        return employee;
    }

    @Override
    public List<Employee> saveAll(Collection<Employee> employees) {
        try {
            lock.lock();
            validateUsers(employees);
            return saveAllUsersToCollection(employees);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Department, List<Employee>> findUsersByDepartment() {
        return employees.stream()
                .collect(
                        groupingBy(
                                Employee::department,
                                mapping(identity(), toList())
                        )
                );
    }

    @Override
    public List<Employee> clear() {
        final List<Employee> employees = new ArrayList<>(this.employees);
        this.employees.clear();
        return employees;
    }

    private void validateUsers(Collection<Employee> employees) {
        checkUserNamesForNulls(employees);
        checkPassedUserNamesForDuplicates(employees);
        checkUserNamesForDuplicatesWithSaved(employees);
    }

    private void checkUserNamesForNulls(Collection<Employee> employees) {
        if (containsAtLeastOneNameNull(employees)) {
            throw new IllegalArgumentException("Cannot save users with null names");
        }
    }

    private boolean containsAtLeastOneNameNull(Collection<Employee> employees) {
        return employees.stream()
                .map(USER_TO_NAME)
                .anyMatch(IS_NULL);
    }

    private void checkPassedUserNamesForDuplicates(Collection<Employee> employees) {
        if (countDistinctUserNames(employees) != employees.size()) {
            throw new IllegalArgumentException("Cannot save users with duplicate names");
        }
    }

    private long countDistinctUserNames(Collection<Employee> employees) {
        return employees.stream()
                .map(USER_TO_NAME)
                .distinct()
                .count();
    }

    private void checkUserNamesForDuplicatesWithSaved(Collection<Employee> employees) {
        final Consumer<Employee> checkUserNameForDuplicatesWithSave = this::checkUserNameDuplicates;
        employees.forEach(checkUserNameForDuplicatesWithSave);
    }

    private List<Employee> saveAllUsersToCollection(Collection<Employee> employees) {
        final Function<Employee, Employee> userToSavedUserWithId = this::saveUserToCollection;
        return employees.stream()
                .map(userToSavedUserWithId)
                .collect(toList());
    }

    private Employee saveUserToCollection(Employee entity) {
        final Employee result = new Employee(++size, entity.name(), entity.department());
        employees.add(result);
        return result;
    }

    private void checkUserNameDuplicates(Employee entity) {
        if (userWithSuchNameAlreadyExists(entity.name())) {
            throw new IllegalArgumentException("User with such name already present");
        }
    }

    private boolean userWithSuchNameAlreadyExists(String name) {
        return employees.stream()
                .map(USER_TO_NAME)
                .anyMatch(name::equals);
    }
}
