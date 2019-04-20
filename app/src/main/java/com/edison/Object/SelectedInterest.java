package com.edison.Object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vengat G on 1/21/2019.
 */

public class SelectedInterest extends RealmObject{

    @PrimaryKey
    private int subcategory_id;

    private String category_id;
    private String subcategory_name;
    private String selected;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }


    public int getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(int subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

}