package com.helpdeskspringapi.helpdesk.factory;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;

public class CategoryFactory {

    public static Category createCategory() {

        Category category = new Category("DNS", "Sites nao abrem por nome, mas abrem por IP, ou nslookup falha.");
        category.setId(1L);
        return category;
    }

    public static CategoryDTO createCategoryDTO() {
        return new CategoryDTO(createCategory());
    }

}
