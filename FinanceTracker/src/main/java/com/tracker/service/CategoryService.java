package com.tracker.service;

import com.tracker.DTO.CategoryDTO;
import com.tracker.entity.Category;
import com.tracker.error.ReusableConstants;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.CategoryRepo;
import net.mguenther.idem.flake.Flake64L;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Flake64L idGenerator;

    public List<CategoryDTO> getCategoryList(Long userId) {
        return categoryRepo.findByUserIdOrderByName(userId,0L).stream().map(this::handleDTO).toList();
    }

    public CategoryDTO createCategory(Long userId, Map<String, ?> input) {
        Category category = modelMapper.map(input,Category.class);
        category.setCategoryId(idGenerator.nextId());
        category.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
        category.setUserId(userId);
        return handleDTO(categoryRepo.save(category));
    }

    public CategoryDTO updateCategory(Long userId,Long categoryId, Map<String, ?> input) {
        Category category = categoryRepo.findByUserIdAndCategoryCategoryId(categoryId,userId,0L);

        if (category == null) throw new FinTrackerException(ErrorConstants.INVALID_INPUT);

        if (input.containsKey(ReusableConstants.NAME)) category.setName(input.get(ReusableConstants.NAME).toString());
        if (input.containsKey(ReusableConstants.DESCRIPTION)) category.setDescription(input.get(ReusableConstants.DESCRIPTION).toString());
        if(input.containsKey(ReusableConstants.MONTHLY_LIMIT)) category.setCurrentMonthlyLimit(Long.valueOf(input.get(ReusableConstants.MONTHLY_LIMIT).toString()));

        category.setUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));

        return handleDTO(categoryRepo.save(category));
    }

    public void deleteCategory(Long userId,Long categoryId) {
        categoryRepo.softDeleteCategory(userId, categoryId, LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public CategoryDTO handleDTO(Category category){
        return new CategoryDTO(category.getCategoryId(),category.getName(),category.getDescription(),category.getCurrentMonthlyLimit());
    }
}
