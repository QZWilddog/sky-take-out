package cn.zimeedu.sky.service;


import cn.zimeedu.sky.dto.EmployeeDTO;
import cn.zimeedu.sky.dto.EmployeeLoginDTO;
import cn.zimeedu.sky.dto.EmployeePageQueryDTO;
import cn.zimeedu.sky.entity.Employee;
import cn.zimeedu.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     *
     * */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 设置员工状态
     * */
    void setStatus(Integer status,  Long id);

    /**
     * 根据id查询员工
     * */
    Employee getById(Long id);

    /**
     * 更改员工信息
     * */
    void update(EmployeeDTO employeeDTO);
}
