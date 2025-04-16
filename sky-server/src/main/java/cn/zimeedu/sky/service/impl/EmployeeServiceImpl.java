package cn.zimeedu.sky.service.impl;


import cn.zimeedu.sky.dto.EmployeeLoginDTO;
import cn.zimeedu.sky.mapper.EmployeeMapper;
import cn.zimeedu.sky.service.EmployeeService;
import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.constant.StatusConstant;

import cn.zimeedu.sky.entity.Employee;
import cn.zimeedu.sky.exception.AccountLockedException;
import cn.zimeedu.sky.exception.AccountNotFoundException;
import cn.zimeedu.sky.exception.PasswordErrorException;
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

}
