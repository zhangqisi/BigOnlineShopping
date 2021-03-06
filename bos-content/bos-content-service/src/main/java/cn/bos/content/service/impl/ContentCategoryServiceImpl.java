package cn.bos.content.service.impl;


import cn.bos.common.pojo.EasyUITreeNode;
import cn.bos.common.utils.E3Result;
import cn.bos.content.service.ContentCategoryService;
import cn.bos.mapper.TbContentCategoryMapper;
import cn.bos.pojo.TbContentCategory;
import cn.bos.pojo.TbContentCategoryExample;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
     * @ProjectName:    内容分类管理
     * @Package:        cn.bos.content.service.impl
     * @ClassName:      ContentCategoryServiceImpl
     * @Description:    java类作用描述
     * @Author:         胡铭锋
     * @CreateDate:     2018/5/23 18:01
     * @Version:        1.0
     */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {


    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;
    private Logger log;

    @Override
    public List<EasyUITreeNode> getContentCategoryList(long parentId) {

        // 1、取查询参数id，parentId
        // 2、根据parentId查询tb_content_category，查询子节点列表。
        TbContentCategoryExample example = new TbContentCategoryExample();
        //设置查询条件
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        // 3、得到List<TbContentCategory>
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        // 4、把列表转换成List<EasyUITreeNode>ub
        List<EasyUITreeNode> resultList = new ArrayList<>();
        for (TbContentCategory tbContentCategory : list) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent()?"closed":"open");
            //添加到列表
            resultList.add(node);
        }
        return resultList;

    }


    @Override
    public E3Result addContentCategory(long parentId, String name) {
        log.debug("dddd");
        // 1、接收两个参数：parentId、name
        // 2、向tb_content_category表中插入数据。
        // a)创建一个TbContentCategory对象
        TbContentCategory tbContentCategory = new TbContentCategory();
        // b)补全TbContentCategory对象的属性
        tbContentCategory.setIsParent(false);
        tbContentCategory.setName(name);
        tbContentCategory.setParentId(parentId);
        //排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
        tbContentCategory.setSortOrder(1);
        //状态。可选值:1(正常),2(删除)
        tbContentCategory.setStatus(1);
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setUpdated(new Date());
        // c)向tb_content_category表中插入数据
        contentCategoryMapper.insert(tbContentCategory);
        // 3、判断父节点的isparent是否为true，不是true需要改为true。
        TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parentNode.getIsParent()) {
            parentNode.setIsParent(true);
            //更新父节点
            contentCategoryMapper.updateByPrimaryKey(parentNode);
        }
        // 4、需要主键返回。
        // 5、返回E3Result，其中包装TbContentCategory对象
        return E3Result.ok(tbContentCategory);
    }


}
