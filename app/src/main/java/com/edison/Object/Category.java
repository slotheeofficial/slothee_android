package com.edison.Object;

/**
 * Created by Vengat G on 1/3/2019.
 */

public class Category {

    String subcategory_id,subcategory_name;
    private String selected;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }


    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

}
