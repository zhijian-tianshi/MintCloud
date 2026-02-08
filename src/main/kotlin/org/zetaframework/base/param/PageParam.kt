package org.zetaframework.base.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid

/**
 * 分页查询参数
 *
 * @author gcc
 */
@ApiModel(description = "分页查询参数")
class PageParam<T> private constructor(){
    /** 当前页 */
    @ApiModelProperty(value = "当前页", example = "1", required = true)
    var page: Long = 1

    /** 每页显示条数 */
    @ApiModelProperty(value = "每页显示条数", example = "10", required = true)
    var limit: Long = 10

    /** 查询条件 */
    @ApiModelProperty(value = "查询条件", required = true)
    @Valid  // 见[docs/03功能介绍/参数校验.md]常见问题
    var model: T? = null

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段", allowableValues = "id,createTime,updateTime", example = "id", required = false)
    var sort: String? = "id"

    /** 排序规则 */
    @ApiModelProperty(value = "排序规则", allowableValues = "desc,asc", example = "desc", required = false)
    var order: String? = "desc"


    constructor(page: Long, limit: Long): this() {
        this.page = page
        this.limit = limit
    }

    constructor(page: Long, limit: Long, model: T? = null): this(page, limit) {
        this.model = model
    }
}
