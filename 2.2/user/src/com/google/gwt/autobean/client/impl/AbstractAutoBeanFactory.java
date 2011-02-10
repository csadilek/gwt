/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.autobean.client.impl;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import com.google.gwt.autobean.shared.impl.EnumMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides base implementations of AutoBeanFactory methods.
 */
public abstract class AbstractAutoBeanFactory implements AutoBeanFactory,
    EnumMap {
  /**
   * Implementations generated by subtypes. Used to implement the dynamic create
   * methods.
   */
  protected interface Creator {
    AutoBean<?> create();

    AutoBean<?> create(Object delegate);
  }

  protected final Map<Class<?>, Creator> creators = new HashMap<Class<?>, Creator>();
  protected Map<Enum<?>, String> enumToStringMap;
  // This map is almost always one-to-one
  protected Map<String, List<Enum<?>>> stringsToEnumsMap;

  @SuppressWarnings("unchecked")
  public <T> AutoBean<T> create(Class<T> clazz) {
    Creator c = creators.get(clazz);
    return c == null ? null : (AutoBean<T>) c.create();
  }

  @SuppressWarnings("unchecked")
  public <T, U extends T> AutoBean<T> create(Class<T> clazz, U delegate) {
    Creator c = creators.get(clazz);
    return c == null ? null : (AutoBean<T>) c.create(delegate);
  }

  /**
   * EnumMap support.
   */
  public <E extends Enum<E>> E getEnum(Class<E> clazz, String token) {
    maybeInitializeEnumMap();
    List<Enum<?>> list = stringsToEnumsMap.get(token);
    if (list == null) {
      throw new IllegalArgumentException(token);
    }
    for (Enum<?> e : list) {
      if (e.getDeclaringClass().equals(clazz)) {
        @SuppressWarnings("unchecked")
        E toReturn = (E) e;
        return toReturn;
      }
    }
    throw new IllegalArgumentException(clazz.getName());
  }

  /**
   * EnumMap support.
   */
  public String getToken(Enum<?> e) {
    maybeInitializeEnumMap();
    String toReturn = enumToStringMap.get(e);
    if (toReturn == null) {
      throw new IllegalArgumentException(e.toString());
    }
    return toReturn;
  }

  protected abstract void initializeEnumMap();

  private void maybeInitializeEnumMap() {
    if (enumToStringMap == null) {
      enumToStringMap = new HashMap<Enum<?>, String>();
      stringsToEnumsMap = new HashMap<String, List<Enum<?>>>();
      initializeEnumMap();
    }
  }
}