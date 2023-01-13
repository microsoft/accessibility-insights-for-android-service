// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ATFAResultsSerializerTest {

  GsonBuilder gsonBuilder = mock(GsonBuilder.class, RETURNS_SELF);
  @Mock Gson gson;
  @Captor ArgumentCaptor<FieldNamingStrategy> fieldNamingStrategy;
  @Captor ArgumentCaptor<ExclusionStrategy> exclusiontStrategy;
  JsonSerializer<Class> jsonSerializer;
  ATFAResultsSerializer testSubject;

  class TestClass {
    public String testField;
  }

  @Before
  public void prepare() {

    doAnswer(
            AdditionalAnswers.answer(
                (Type type, JsonSerializer<Class> serializer) -> {
                  jsonSerializer = serializer;
                  return gsonBuilder;
                }))
        .when(gsonBuilder)
        .registerTypeAdapter(eq(Class.class), any());

    when(gsonBuilder.create()).thenReturn(gson);

    testSubject = new ATFAResultsSerializer(gsonBuilder);
    verify(gsonBuilder).setExclusionStrategies(exclusiontStrategy.capture());
    verify(gsonBuilder).setFieldNamingStrategy(fieldNamingStrategy.capture());
  }

  @Test
  public void fieldNamingStrategyReturnsName() {
    class ExtendingClass extends TestClass {
      public String testField;
    }
    Field[] testFields = ExtendingClass.class.getFields();

    String testFieldExtendingClass = fieldNamingStrategy.getValue().translateName(testFields[0]);
    String testFieldBaseClass = fieldNamingStrategy.getValue().translateName(testFields[1]);

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

    classesToExclude.forEach(
        c -> Assert.assertTrue(exclusiontStrategy.getValue().shouldSkipClass(c)));
    classesToInclude.forEach(
        c -> Assert.assertFalse(exclusiontStrategy.getValue().shouldSkipClass(c)));
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
