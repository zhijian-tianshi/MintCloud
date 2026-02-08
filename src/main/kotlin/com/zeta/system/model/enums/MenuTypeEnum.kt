package com.zeta.system.model.enums

import com.mybatisflex.annotation.EnumValue
import io.swagger.annotations.ApiModel

/**
 * 菜单类型
 * @author gcc
 */
@ApiModel(description = "菜单类型 枚举")
enum class MenuTypeEnum {
    /** 菜单 */
    MENU,
    /** 资源 */
    RESOURCE;

    /**
     * 枚举数据库存储值
     */
    @EnumValue
     fun getValue(): String {
        return this.name
    }
}
