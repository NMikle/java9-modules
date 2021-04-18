package io.mikle.test.mds.core;

import io.mikle.test.mds.api.model.Department;
import io.mikle.test.mds.api.model.Employee;
import io.mikle.test.mds.api.service.EmployeeService;
import io.mikle.test.mds.core.service.ListEmployeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListEmployeeServiceTest {

    public static final Department SALES = new Department("sales");
    public static final Department DEVELOPMENT = new Department("development");

    private static final List<Employee> dbContent = List.of(
            new Employee("James", SALES),
            new Employee("Alice", DEVELOPMENT),
            new Employee("Bob", SALES)
    );
    public static final String DUPLICATE_NAME_MSG = "User with such name already present";

    private final EmployeeService service = new ListEmployeeService();

    @BeforeEach
    void setUp() {
        service.saveAll(dbContent);
    }

    @AfterEach
    void tearDown() {
        service.clear();
    }

    @Test
    public void testFindAll_shouldReturnAll_always() {
        final List<Employee> actual = service.findAll();
        assertEquals(dbContent.size(), actual.size());
    }

    @Test
    public void testSave_shouldSaveAndReturnEmployeeWithId_whenNameValid() {
        final Employee john = new Employee("John", DEVELOPMENT);
        final Employee actual = service.save(john);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertSame(john.name(), actual.name());
        assertSame(john.department(), actual.department());
        assertEquals(retrieveCurrentMaxId(), actual.id());
    }

    @Test
    public void testSave_shouldThrowIllegalArgument_whenNameAlreadyPresent() {
        final Employee james = new Employee("James", DEVELOPMENT);
        final IllegalArgumentException actualEx = assertThrows(
                IllegalArgumentException.class,
                () -> service.save(james)
        );
        assertEquals(DUPLICATE_NAME_MSG, actualEx.getMessage());
    }

    @Test
    public void testFindById_shouldReturnEmployeeById_whenIdValid() {
        final Employee actualEmp = service.findById(1);
        assertEquals(dbContent.get(0).name(), actualEmp.name());
        assertEquals(dbContent.get(0).department(), actualEmp.department());
    }

    private Integer retrieveCurrentMaxId() {
        return service.findAll().stream()
                .map(Employee::id)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

}
