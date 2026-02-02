package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
