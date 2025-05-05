package cn.zimeedu.sky.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("接收员工更新密码数据模型")
public class EmployeePassWordDTO implements Serializable {

    private Long empId;

    private String newPassword;

    private String oldPassword;
}
