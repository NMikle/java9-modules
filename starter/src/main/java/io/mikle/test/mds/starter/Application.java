package io.mikle.test.mds.starter;

import io.mikle.test.mds.api.model.Department;
import io.mikle.test.mds.api.model.Employee;
import io.mikle.test.mds.api.service.EmployeeService;

import java.util.ServiceLoader;

public class Application {

    public static void main(String[] args) {
        System.out.println("Program start");
        final ServiceLoader<EmployeeService> load = ServiceLoader.load(EmployeeService.class);
        final Department sales = new Department("sales");
        final Department dev = new Department("development");
        load.findFirst().ifPresent(userService -> {
            userService.save(new Employee("Tom", sales));
            userService.save(new Employee("Alice", dev));
            userService.save(new Employee("Bob", sales));
        });
        for (EmployeeService service : load) {
            System.out.println(service.findAll());
            System.out.println("-------------------");
            System.out.println(service.findUsersByDepartment());
        }
        System.out.println("Program End");
    }

}
