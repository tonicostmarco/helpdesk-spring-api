package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO(obj.id, obj.name, obj.description) " +
            "FROM Category obj " +
            "JOIN  obj.categoryTickets")
    List<CategoryMinDTO> findAllWithTickets(List<Category> categories);

    boolean existsByName(String name);
}
