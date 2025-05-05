package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.dto.EmployeeDTO;
import cn.zimeedu.sky.dto.EmployeeLoginDTO;
import cn.zimeedu.sky.dto.EmployeePageQueryDTO;
import cn.zimeedu.sky.dto.EmployeePassWordDTO;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.service.EmployeeService;
import cn.zimeedu.sky.constant.JwtClaimsConstant;
import cn.zimeedu.sky.entity.Employee;
import cn.zimeedu.sky.properties.JwtProperties;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.utils.JwtUtil;
import cn.zimeedu.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value =  "员工登陆")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()  // 首先调用 builder() 静态方法，获取一个 Builder 实例。
                .id(employee.getId())              // 就是在这个对象的内部创建一个静态方法和一个静态内部类（这个内部类最终会返回一个该类的全参构造器）
                .userName(employee.getUsername())   // 过 Builder 实例的 setter 方法设置属性值，每个 setter 方法（setter名字由Builder生成 就是与成员变量同名的方法）都返回 Builder 自身，因此可以链式调用
                .name(employee.getName())
                .token(token)
                .build();  // 创建并返回目标对象（new 一个该类的全参构造器 返回该对象）

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<Object> logout() {
        log.info("员工退出");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     * */
    @PostMapping
    @ApiOperation("新增员工")
    public Result<Object> save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工:{}", employeeDTO);

         employeeService.save(employeeDTO);

        return Result.success("添加员工成功");
    }

    /**
     * 员工分页查询
     * */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工{}", employeePageQueryDTO);

        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }
    /**
     * 设置员工账号状态
     * */
    @PostMapping("/status/{status}")
    @ApiOperation("设置员工账号状态")
    public Result<Object> setStatus(@PathVariable Integer status, Long id){
        log.info("设置员工账号状态:{},id:{}", status,id);

        employeeService.setStatus(status, id);

        return Result.success("设置状态成功");
    }

    /**
     * 根据id查询员工
     * */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("查询员工：{}", id);
        Employee employee =  employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 更改员工信息
     * */
    @PutMapping
    public Result<Object> update(@RequestBody EmployeeDTO employeeDTO){
        log.info("更改员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success("修改成功");
    }

    @PutMapping("/editPassword")
    public Result<Object> setPassword(@RequestBody EmployeePassWordDTO employeePassWordDTO){
        log.info("员工密码修改{}", employeePassWordDTO);
        employeePassWordDTO.setEmpId(BaseContext.getCurrentId());
        employeeService.setPassword(employeePassWordDTO);
        return Result.success("修改成功");

    }
}
