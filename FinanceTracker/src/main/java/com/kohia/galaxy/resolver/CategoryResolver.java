package com.kohia.galaxy.resolver;

import com.kohia.galaxy.DTO.CategoryDTO;
import com.kohia.galaxy.service.CategoryService;
import com.kohia.galaxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class CategoryResolver {
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public CategoryResolver(CategoryService categoryService, UserService userService){
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @QueryMapping(name = "getCategoryList")
    public List<CategoryDTO> getCategoryList() {
        return categoryService.getCategoryList(userService.getCurrentUserId());
    }

    @MutationMapping
    public CategoryDTO createCategory(@Argument Map<String, ?> input) {
        return categoryService.createCategory(userService.getCurrentUserId(), input);
    }

    @MutationMapping
    public CategoryDTO updateCategory(@Argument Long categoryId,@Argument Map<String, ?> input) {
        return categoryService.updateCategory(userService.getCurrentUserId(),categoryId,input);
    }

    @MutationMapping
    public long deleteCategory(@Argument Long categoryId) {
        categoryService.deleteCategory(userService.getCurrentUserId(),categoryId);
        return categoryId;
    }
}
