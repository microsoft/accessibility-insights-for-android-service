// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.uielement.ViewHierarchyElement;
import com.google.android.apps.common.testing.accessibility.framework.uielement.WindowHierarchyElement;
import com.google.android.apps.common.testing.accessibility.framework.uielement.WindowHierarchyElementAndroid;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GsonBuilder.class, Gson.class})
public class ATFAResultsSerializerTest {

  @Mock GsonBuilder gsonBuilder;
  @Mock Gson gson;
  FieldNamingStrategy fieldNamingStrategy;
  ExclusionStrategy exclusionStrategy;
  JsonSerializer<Class> jsonSerializer;
  ATFAResultsSerializer testSubject;

  class TestClass {
    public String testField;
  }

  @Before
  public void prepare() {
    doAnswer(
            AdditionalAnswers.answer(
                (FieldNamingStrategy strategy) -> {
                  fieldNamingStrategy = strategy;
                  return gsonBuilder;
                }))
        .when(gsonBuilder)
        .setFieldNamingStrategy(any());

    doAnswer(
            AdditionalAnswers.answer(
                (ExclusionStrategy strategy) -> {
                  exclusionStrategy = strategy;
                  return gsonBuilder;
                }))
        .when(gsonBuilder)
        .setExclusionStrategies(any());

    doAnswer(
            AdditionalAnswers.answer(
                (Type type, JsonSerializer serializer) -> {
                  jsonSerializer = serializer;
                  return gsonBuilder;
                }))
        .when(gsonBuilder)
        .registerTypeAdapter(eq(Class.class), any());

    when(gsonBuilder.create()).thenReturn(gson);

    testSubject = new ATFAResultsSerializer(gsonBuilder);
  }

  @Test
  public void fieldNamingStrategyReturnsName() {
    class ExtendingClass extends TestClass {
      public String testField;
    }
    Field[] testFields = ExtendingClass.class.getFields();

    String testFieldExtendingClass = fieldNamingStrategy.translateName(testFields[0]);
    String testFieldBaseClass = fieldNamingStrategy.translateName(testFields[1]);

    Assert.assertEquals("TestClass.testField", testFieldBaseClass);
    Assert.assertEquals("ExtendingClass.testField", testFieldExtendingClass);
  }

  @Test
  public void exclusionStrategyExcludesWindowHierarchyElements() {
    List<Class> classesToExclude =
        Arrays.asList(WindowHierarchyElement.class, WindowHierarchyElementAndroid.class);
    List<Class> classesToInclude =
        Arrays.stream(ViewHierarchyElement.class.getFields())
            .map(f -> f.getClass())
            .filter(c -> !classesToExclude.contains(c))
            .collect(Collectors.toList());

    classesToExclude.forEach(c -> Assert.assertTrue(exclusionStrategy.shouldSkipClass(c)));
    classesToInclude.forEach(c -> Assert.assertFalse(exclusionStrategy.shouldSkipClass(c)));
  }

  @Test
  public void jsonSerializerSerializesClassName() {
    JsonPrimitive expectedJson = new JsonPrimitive(TestClass.class.getSimpleName());

    JsonElement jsonElement = jsonSerializer.serialize(TestClass.class, Class.class, null);

    Assert.assertEquals(expectedJson, jsonElement);
  }

  @Test
  public void serializeATFAResultsCallsGsonSerializer() {
    List<AccessibilityHierarchyCheckResult> results = Collections.emptyList();

    testSubject.serializeATFAResults(results);

    verify(gson, times(1)).toJson(results);
  }
}
