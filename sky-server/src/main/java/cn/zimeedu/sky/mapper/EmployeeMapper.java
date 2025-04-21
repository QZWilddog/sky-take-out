package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.annotation.AutoFill;
import cn.zimeedu.sky.dto.EmployeeDTO;
import cn.zimeedu.sky.dto.EmployeePageQueryDTO;
import cn.zimeedu.sky.entity.Employee;
import cn.zimeedu.sky.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {  // 员工表管理

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);   // 动词+by+条件

    /**
     * 新增员工
     * @param employee
     *
     * */
    @AutoFill(value = OperationType.INSERT)
    void save(Employee employee);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 动态SQL，类似方法重载 根据主键动态修改属性
     * 所有更改员工操作都在整个方法名里执行
     * */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工
     * */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

}
