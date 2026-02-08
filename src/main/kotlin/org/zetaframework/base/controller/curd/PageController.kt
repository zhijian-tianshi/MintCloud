package org.zetaframework.base.controller.curd

import cn.hutool.core.bean.BeanUtil
import com.mybatisflex.core.paginate.Page
import com.mybatisflex.core.query.QueryWrapper

import org.zetaframework.base.controller.BaseController
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult

/**
 * 分页 Controller
 *
 * @param <Entity>     实体
 * @param <QueryParam>  查询参数
 * @author gcc
 */
interface PageController<Entity, QueryParam>: BaseController<Entity> {

    /**
     * 分页查询
     *
     * @param param PageParam<QueryParam>
     * @return PageResult<Entity>
     */
    fun query(param: PageParam<QueryParam>): PageResult<Entity> {
        // 处理查询参数
        handlerQueryParams(param)

        // 构建分页对象
        val page: Page<Entity> = Page(param.page, param.limit)
        // PageQuery -> Entity
        val model: Entity = BeanUtil.toBean(param.model, getEntityClass())

        // 构造分页查询条件
        val wrapper = handlerWrapper(model, param)
        // 执行单表分页查询
        getBaseService().page(page, wrapper)

        // 处理查询后的分页结果
        handlerResult(page)

        return PageResult(page.records, page.pageSize)
    }


    /**
     * 构造查询条件
     *
     * @param model Entity?
     * @param param PageParam<PageQuery>
     * @return QueryWrapper<Entity>
     */
    fun handlerWrapper(model: Entity?, param: PageParam<QueryParam>): QueryWrapper{
        // ?.let 不为空执行
        return model?.let { QueryWrapper() } ?: QueryWrapper()
    }


    /**
     * 处理查询参数
     *
     * @param param 查询参数
     */
    fun handlerQueryParams(param: PageParam<QueryParam>) { }

    /**
     * 处理查询后的数据
     *
     * @param page IPage
     */
    fun handlerResult(page: Page<Entity>) { }

}
