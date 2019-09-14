package com.hiflink.common.storage.sorting.impl;

import com.hiflink.common.storage.sorting.EntitySortingDirection;
import com.hiflink.common.storage.sorting.ISortingEntity;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

@Getter
public class SortingEntity implements ISortingEntity {

    private String entityName;
    private EntitySortingDirection entitySortingDirection = EntitySortingDirection.ASC;
    private Class<?> entityType = String.class;

    public SortingEntity( String entityName, EntitySortingDirection entitySortingDirection, Class entityType ) {
        this.entityName = entityName;
        this.entitySortingDirection = entitySortingDirection;
        this.entityType = entityType;
    }

    public SortingEntity( String entityName ) {
        this.entityName = entityName;
    }


    public <I, O> O convert( I input, Class<O> outputClass ) throws Exception {
        // return input == null ? null :this.entityType.getConstructor().newInstance(input.toString());
        return input == null ? null : outputClass.getConstructor(String.class).newInstance(input.toString());
    }


    public <I> int  compare( I thisValue, I outValue ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int compareTo = (int) this.entityType.getMethod("compareTo", this.entityType).invoke(outValue, thisValue);
        return compareTo;
        // (int) sortingEntity.entityType.getMethod("compareTo", sortingEntity.entityType).invoke(sortingEntity.convert(s2.get(sortingEntity.entityName), sortingEntity.entityType), sortingEntity.convert(s1.get(sortingEntity.entityName), sortingEntity.entityType));
    }

}


