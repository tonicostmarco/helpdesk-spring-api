package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

       Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id not found"));

        return new CategoryMinDTO(category);

    }

    @Transactional(readOnly = true)
    public List<CategoryMinDTO> findAll() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(CategoryMinDTO::new).collect(Collectors.toList());

    }

    public List<CategoryMinDTO> findAllWithTickets() {

        List<Category> categories = categoryRepository.findAll();

        return categoryRepository.findAllWithTickets(categories.stream().collect(Collectors.toList()));

    }

    @Transactional
    public CategoryMinDTO insert(CategoryMinDTO dto) {

        if(categoryRepository.existsByName(dto.getName())) {
            throw new DatabaseException("Category Already exists");
        }

        Category Category = new Category();
        copyDtoToEntity(dto, Category);

        Category = categoryRepository.save(Category);

        return new CategoryMinDTO(Category);

    }

    @Transactional
    public CategoryMinDTO update(Long id, CategoryMinDTO dto) {

        if (id == null) {
            throw new InvalidParameterException("Id required");
        }
        try {
            Category category = categoryRepository.getReferenceById(id);

            copyDtoToEntity(dto, category);

            category = categoryRepository.save(category);

            return new CategoryMinDTO(category);
        }
        catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Category not found");
        }

    }

    @Transactional
    public void delete(Long id) {

        try {
            categoryRepository.deleteById(id);
        }
        catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Category not found");
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }

    }

    private void copyDtoToEntity(CategoryMinDTO dto, Category category) {
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

    }



}
