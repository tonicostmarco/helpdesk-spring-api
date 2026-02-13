package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TicketRepository ticketRepository;


    @Transactional(readOnly = true)
    public CategoryMinDTO findById(Long id) {

       Category category = categoryRepository.findById(id).orElseThrow();

        return new CategoryMinDTO(category);

    }

    @Transactional(readOnly = true)
    public List<CategoryMinDTO> findAll() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(CategoryMinDTO::new).collect(Collectors.toList());

    }

    @Transactional
    public CategoryMinDTO insert(CategoryMinDTO dto) {

        Category Category = new Category();
        copyDtoToEntity(dto, Category);

        Category = categoryRepository.save(Category);

        return new CategoryMinDTO(Category);

    }

    @Transactional
    public CategoryMinDTO update(Long id, CategoryMinDTO dto) {

        Category category = categoryRepository.getReferenceById(id);

        copyDtoToEntity(dto, category);

        category = categoryRepository.save(category);

        return new CategoryMinDTO(category);

    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    private void copyDtoToEntity(CategoryMinDTO dto, Category category) {
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

    }



}
