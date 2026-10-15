package com.careerassistant.dto;

import com.careerassistant.entity.SkillCategory;

public class CategorizedSkillDTO {

    private String name;
    private SkillCategory category;

    public CategorizedSkillDTO() {
    }

    public CategorizedSkillDTO(String name, SkillCategory category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }
}
