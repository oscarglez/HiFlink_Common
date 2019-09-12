package com.hiflink.common.utils.resources;

import java.io.Serializable;

/**
 * The options of resources.
 */
public enum ResourceParams implements Serializable {
    CLASS_FROM_READING, FILE_NAME, PATTERN, FILE_EXTENSION, TEXT_ENCODING,
    S3_USE, S3_ACCESS_KEY, S3_SECRET_KEY, S3_END_POINT_URL, S3_BUCKET_NAME, S3_FILE_KEY, S3_FILES_KEYS, DIRECTORY_KEY;

    private String name;

    private ResourceParams() {
        this.name = this.name().toLowerCase();
    }

    public String getName() {
        return name;
    }
}