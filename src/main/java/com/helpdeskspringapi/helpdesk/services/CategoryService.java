package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.CategoryDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TicketRepository ticketRepository;


    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {

       Category category = categoryRepository.findById(id).orElseThrow();

        return new CategoryDTO(category);

    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {

        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories.map(CategoryDTO::new);

    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {

        Category Category = new Category();
        copyDtoToEntity(dto, Category);

        Category = categoryRepository.save(Category);

        return new CategoryDTO(Category);

    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {

        Category category = categoryRepository.getReferenceById(id);

        copyDtoToEntity(dto, category);

        category = categoryRepository.save(category);

        return new CategoryDTO(category);

    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    private void copyDtoToEntity(CategoryDTO dto, Category category) {
        category.setId(dto.getId());
        category.setName(dto.getName());

    }

}
