package com.atchen.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author atchen
 * @since 2023-08-29
 */
@Data
@TableName("x_user")
public class User implements Serializable {
    @TableLogic
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private Integer status;

    private String avatar;
    @TableField(exist = false)
    private List<Integer> roleIdList = new ArrayList<>();

}
