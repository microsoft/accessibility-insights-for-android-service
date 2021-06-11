// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck;
import com.google.common.collect.ImmutableSet;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Arrays;
import java.util.List;

public class ATFARulesSerializer {
  private static final List<String> fieldsToSkip = Arrays.asList("ANDROID_A11Y_HELP_URL");

  private static final ExclusionStrategy ATFAExclusionStrategy =
    new ExclusionStrategy() {
      @Override
      public boolean shouldSkipField(FieldAttributes f) {
        return f.getName().equals("ANDROID_A11Y_HELP_URL");
      }

      @Override
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }
    };

  private static final JsonSerializer<Class<? extends AccessibilityCheck>> ClassSerializer =
      (src, typeOfSrc, context) -> new JsonPrimitive(src.getSimpleName());

  private final Gson gsonSerializer;

  public ATFARulesSerializer(GsonBuilder gsonBuilder) {
    gsonSerializer = gsonBuilder.serializeNulls()
        .setPrettyPrinting()
        .excludeFieldsWithModifiers()
        .create();
  }

  public String serializeATFARules() {
    ImmutableSet<AccessibilityHierarchyCheck> presetChecks = AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST);
    
    return gsonSerializer.toJson(presetChecks);
  }
}
