package cn.zimeedu.sky.service.impl;


import cn.zimeedu.sky.constant.PasswordConstant;
import cn.zimeedu.sky.dto.EmployeeDTO;
import cn.zimeedu.sky.dto.EmployeeLoginDTO;
import cn.zimeedu.sky.dto.EmployeePageQueryDTO;
import cn.zimeedu.sky.mapper.EmployeeMapper;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.service.EmployeeService;
import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.constant.StatusConstant;

import cn.zimeedu.sky.entity.Employee;
import cn.zimeedu.sky.exception.AccountLockedException;
import cn.zimeedu.sky.exception.AccountNotFoundException;
import cn.zimeedu.sky.exception.PasswordErrorException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //进行md5加密，然后进行比对        使用 DigestUtils 计算 MD5 哈希值        DigestUtils工具类，主要用于简化加密哈希算法的操作  md5DigestAsHex使用 MD5 算法计算其哈希值
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));  // 这是因为哈希函数（如 MD5）需要处理的是二进制数据，而不是直接处理字符串 指定字符集为UTF-8  StandardCharsets常用字符集的常量引用
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     *
     * */
    @Override
    public void save(EmployeeDTO employeeDTO){
        Employee employee = new Employee();

        // 对象属性拷贝     Spring Framework 中提供的一个工具方法，用于简化 Java 对象之间的属性复制操作
        BeanUtils.copyProperties(employeeDTO, employee);  // 主要作用是将源对象（source）中的属性值复制到目标对象（target）中。它会根据属性名进行匹配，并将源对象中与目标对象同名的属性值赋值给目标对象。

        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8)));

        employeeMapper.save(employee);
    }


    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        // 设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());  // PageHelper 插件会将查询结果封装到 Page 对象中，而不是普通的 List, 用list：会丢失 PageHelper 提供的分页元信息（如总记录数、总页数等

        Page<Employee> employeePage = employeeMapper.pageQuery(employeePageQueryDTO);

        return new PageResult(employeePage.getTotal(), employeePage.getResult());
    }

    /**
     * 设置员工账号状态
     * 动态SQL，类似方法重载 根据主键动态修改属性  所有更改员工操作都在整个方法名里执行
     * */
    @Override
    public void setStatus(Integer status,  Long id){
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);

    }

    /**
     * 根据id查询员工
     * */
    @Override
    public Employee getById(Long id){
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 更改员工信息
     * */
    @Override
    public void update(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employeeMapper.update(employee);
    }
}
