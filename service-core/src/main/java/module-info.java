import io.mikle.test.mds.api.service.EmployeeService;
import io.mikle.test.mds.core.service.ListEmployeeService;

module mds.service.core {
    requires mds.service.api;
    provides EmployeeService with ListEmployeeService;
}