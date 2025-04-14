package cn.zimeedu.sky.service;


import cn.zimeedu.sky.dto.EmployeeLoginDTO;
import cn.zimeedu.sky.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
