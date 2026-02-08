package com.zeta.system.model.entity


import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.zetaframework.base.entity.SuperEntity
import javax.validation.constraints.NotBlank

/**
 * 操作日志
 *
 * @author gcc
 * @date 2022-03-18 15:27:15
 */
@ApiModel(description = "操作日志")
@Table(value = "sys_opt_log")
class SysOptLog : SuperEntity<Long>() {

    /** 操作类型 */
    @ApiModelProperty(value = "操作类型", required = true)
    @Column(value = "type")
    var type: String? = null

    /** 操作描述 */
    @ApiModelProperty(value = "操作描述", required = true)
    @Column(value = "description")
    var description: String? = null

    /** 请求地址 */
    @ApiModelProperty(value = "请求地址", required = true)
    @get:NotBlank(message = "请求地址不能为空")
    @Column(value = "url")
    var url: String? = null

    /** 请求方式 */
    @ApiModelProperty(value = "请求方式", required = true)
    @get:NotBlank(message = "请求方式不能为空")
    @Column(value = "http_method")
    var httpMethod: String? = null

    /** 类路径 */
    @ApiModelProperty(value = "类路径", required = true)
    @get:NotBlank(message = "类路径不能为空")
    @Column(value = "class_path")
    var classPath: String? = null

    /** 请求参数 */
    @ApiModelProperty(value = "请求参数", required = false)
    @Column(value = "params")
    var params: String? = null

    /** 返回值 */
    @ApiModelProperty(value = "返回值", required = false)
    @Column(value = "result")
    var result: String? = null

    /** 异常描述 */
    @ApiModelProperty(value = "异常描述", required = false)
    @Column(value = "exception")
    var exception: String? = null

    /** 消耗时间 单位毫秒 */
    @ApiModelProperty(value = "消耗时间 单位毫秒", required = true)
    @get:NotBlank(message = "消耗时间不能为空")
    @Column(value = "spend_time")
    var spendTime: Int? = null

    /** 操作系统 */
    @ApiModelProperty(value = "操作系统", required = false)
    @Column(value = "os")
    var os: String? = null

    /** 设备名称 */
    @ApiModelProperty(value = "设备名称", required = false)
    @Column(value = "device")
    var device: String? = null

    /** 浏览器类型 */
    @ApiModelProperty(value = "浏览器类型", required = false)
    @Column(value = "browser")
    var browser: String? = null

    /** ip地址 */
    @ApiModelProperty(value = "ip地址", required = false)
    @Column(value = "ip")
    var ip: String? = null

    /** ip所在地区 */
    @ApiModelProperty(value = "ip所在地区", required = false)
    @Column(value = "ip_region")
    var ipRegion: String? = null

    /** 操作人 */
    @ApiModelProperty(value = "操作人", required = false)
    @Column(ignore = false)
    var userName: String? = null

    override fun toString(): String {
        return "SysOptLog(id=$id, createTime=$createTime, createdBy=$createdBy, type=$type, description=$description, url=$url, httpMethod=$httpMethod, classPath=$classPath, params=$params, result=$result, exception=$exception, spendTime=$spendTime, os=$os, device=$device, browser=$browser, ip=$ip, ipRegion=$ipRegion)"
    }

}
