package io.mikle.test.mds.api.service;

import io.mikle.test.mds.api.model.Department;
import io.mikle.test.mds.api.model.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EmployeeService extends GeneralService<Employee> {

    List<Employee> saveAll(Collection<Employee> employees);

    Map<Department, List<Employee>> findUsersByDepartment();

    List<Employee> clear();
}
