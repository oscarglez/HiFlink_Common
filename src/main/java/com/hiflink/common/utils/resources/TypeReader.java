package com.hiflink.common.utils.resources;

import java.io.Serializable;

/**
     * The type of a schema.
     */
    public enum TypeReader implements Serializable {
        FILE, S3, CLOUD;

        private String name;

        private TypeReader() {
            this.name = this.name().toLowerCase();
        }

        public String getName() {
            return name;
        }
    }