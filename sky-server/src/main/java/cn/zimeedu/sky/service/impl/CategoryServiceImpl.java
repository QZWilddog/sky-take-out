package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.constant.StatusConstant;
import cn.zimeedu.sky.dto.CategoryDTO;
import cn.zimeedu.sky.dto.CategoryPageQueryDTO;
import cn.zimeedu.sky.entity.Category;
import cn.zimeedu.sky.exception.DeletionNotAllowedException;
import cn.zimeedu.sky.mapper.CategoryMapper;
import cn.zimeedu.sky.mapper.DishMapper;
import cn.zimeedu.sky.mapper.SetmealMapper;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.service.CategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        Page<Category> categoryPage = categoryMapper.page(categoryPageQueryDTO);

        return new PageResult(categoryPage.getTotal(), categoryPage.getResult());
    }

    @Override
    public void setStatus(Integer status, Long id) {
        Category category = Category.builder()
                        .status(status)
                        .id(id)
                        .build();

        categoryMapper.update(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.update(category);
    }

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);

        categoryMapper.save(category);
    }

    @Override
    public void deleteById(Long id) {
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        count = setmealMapper.countByCategoryId(id);
        if (count > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    @Override
    public List<Category> listUser(Integer type, Integer status) {

        return categoryMapper.listUser(type, status);
    }
}
