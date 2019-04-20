package com.edison.Object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vengat G on 1/22/2019.
 */

public class SelectedImage extends RealmObject {

    @PrimaryKey
    private String originalImagePath;
    private String compressedImagePath;

    public String getOriginalImagePath() {
        return originalImagePath;
    }

    public void setOriginalImagePath(String originalImagePath) {
        this.originalImagePath = originalImagePath;
    }

    public String getCompressedImagePath() {
        return compressedImagePath;
    }

    public void setCompressedImagePath(String compressedImagePath) {
        this.compressedImagePath = compressedImagePath;
    }
}
