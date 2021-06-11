// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.uielement.WindowHierarchyElement;
import com.google.android.apps.common.testing.accessibility.framework.uielement.WindowHierarchyElementAndroid;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.util.Arrays;
import java.util.List;

public class ATFAResultsSerializer {
  private static final List<Class> ClassesToSkip =
      Arrays.asList(WindowHierarchyElement.class, WindowHierarchyElementAndroid.class);

  private static final FieldNamingStrategy ATFAFieldNamingStrategy =
      f -> f.getDeclaringClass().getSimpleName() + "." + f.getName();

  private static final ExclusionStrategy ATFAExclusionStrategy =
      new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
          return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
          return ClassesToSkip.contains(clazz);
        }
      };

  private static final JsonSerializer<Class<? extends AccessibilityCheck>> ClassSerializer =
      (src, typeOfSrc, context) -> new JsonPrimitive(src.getSimpleName());

  private final Gson gsonSerializer;

  public ATFAResultsSerializer(GsonBuilder gsonBuilder) {
    gsonSerializer =
        gsonBuilder
            .setFieldNamingStrategy(ATFAFieldNamingStrategy)
            .setExclusionStrategies(ATFAExclusionStrategy)
            .registerTypeAdapter(Class.class, ClassSerializer)
            .create();
  }

  public String serializeATFAResults(List<AccessibilityHierarchyCheckResult> atfaResults) {
    return gsonSerializer.toJson(atfaResults);
  }
}
