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
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ATFARulesSerializer {
  private static final List<String> FieldsToSkip = Arrays.asList("ANDROID_A11Y_HELP_URL");

  private static final ExclusionStrategy ATFARuleExclusionStrategy =
    new ExclusionStrategy() {
      @Override
      public boolean shouldSkipField(FieldAttributes f) {
        return FieldsToSkip.contains(f.getName());
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
        .registerTypeAdapter(AccessibilityHierarchyCheck.class, new AccessibilityHierarchyCheckAdapter())
        .create();
  }

  public String serializeATFARules() {
    ImmutableSet<AccessibilityHierarchyCheck> presetChecks = AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST);
    
    return gsonSerializer.toJson(presetChecks);
  }

  private String serializeRuleIdsAndMetadata(AccessibilityHierarchyCheck check) {
    Gson gson = new GsonBuilder()
      .serializeNulls()
      .setPrettyPrinting()
      .excludeFieldsWithModifiers()
      .setExclusionStrategies(ATFARuleExclusionStrategy)
      .registerTypeAdapter(Class.class, ClassSerializer)
      .create();
    return gson.toJson(check);
  }

  private class AccessibilityHierarchyCheckAdapter extends TypeAdapter<AccessibilityHierarchyCheck> {
    @Override
    public void write(JsonWriter out, AccessibilityHierarchyCheck value) throws IOException {
      out.beginObject();
      out.name("class").value(value.getClass().getName());
      out.name("titleMessage").value(value.getTitleMessage(Locale.getDefault()));
      out.name("category").value(String.valueOf(value.getCategory()));
      out.name("helpUrl").value(value.getHelpUrl());
      out.name("resultIdsAndMetadata").jsonValue(serializeRuleIdsAndMetadata(value));
      out.endObject();
    }

    @Override
    public AccessibilityHierarchyCheck read(JsonReader in) throws IOException {
      return null;
    }
  }
}
