package org.zetaframework.base.entity


import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.core.keygen.KeyGenerators
import io.swagger.annotations.ApiModelProperty
import org.zetaframework.core.validation.group.Update
import java.io.Serializable
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

/**
 * 包括id、create_time、create_by字段的表继承的基础实体
 *
 * @author gcc
 * @date 2021/10/18 下午2:23
 * @since 1.0.0
 */
abstract class SuperEntity<T>(
    /** id */
    @Column(value = FIELD_ID)
    @ApiModelProperty(value = "主键")
    @get:NotNull(message = "id不能为空",groups = [Update::class])
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    open var id: T? = null,

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间")
    @Column(value = CREATE_TIME_COLUMN,onInsertValue = "now()")
    open var createTime: LocalDateTime? = null,

    /** 创建人ID */
    @ApiModelProperty(value = "创建人ID")
    @Column(value = CREATED_BY_COLUMN)
    open var createdBy: T? = null,
): Serializable {

    companion object {
        const val FIELD_ID = "id"
        const val CREATE_TIME = "createTime"
        const val CREATE_TIME_COLUMN = "create_time"
        const val CREATED_BY = "createdBy"
        const val CREATED_BY_COLUMN = "created_by"
    }

}
