package com.zeta.system.model.entity


import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.zetaframework.base.entity.SuperEntity
import javax.validation.constraints.NotNull

/**
 * 角色菜单
 *
 * @author AutoGenerator
 * @date 2021-12-30 15:24:03
 */
@ApiModel(description = "角色菜单")
@Table(value = "sys_role_menu")
class SysRoleMenu(): SuperEntity<Long>() {

    /** 角色id */
    @ApiModelProperty(value = "角色id", required = true)
    @get:NotNull(message = "角色id不能为空")
    @Column(value = "role_id")
    var roleId: Long? = null

    /** 菜单id */
    @ApiModelProperty(value = "菜单id", required = true)
    @get:NotNull(message = "菜单id不能为空")
    @Column(value = "menu_id")
    var menuId: Long? = null

    constructor(roleId: Long?, menuId: Long?): this() {
        this.roleId = roleId
        this.menuId = menuId
    }

    override fun toString(): String {
        return "SysRoleMenu(id=$id, createTime=$createTime, createdBy=$createdBy, roleId=$roleId, menuId=$menuId)"
    }

}
