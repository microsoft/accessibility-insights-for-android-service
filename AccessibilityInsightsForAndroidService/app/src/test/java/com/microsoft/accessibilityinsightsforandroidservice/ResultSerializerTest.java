// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deque.axe.android.AxeResult;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
public class ResultSerializerTest {

  @Mock AxeResult axeResultMock;
  @Mock ATFAResultsSerializer atfaResultsSerializer;
  @Mock GsonBuilder gsonBuilder;
  @Mock JsonWriter jsonWriter;
  @Mock Gson gson;

  final List<AccessibilityHierarchyCheckResult> atfaResults = Collections.emptyList();
  final ResultsContainer resultsContainer = new ResultsContainer();

  TypeAdapter<ResultsContainer> resultsContainerTypeAdapter;
  ResultSerializer testSubject;

  @Before
  public void prepare() {
    doAnswer(
            AdditionalAnswers.answer(
                (Type type, TypeAdapter<ResultsContainer> typeAdapter) -> {
                  resultsContainerTypeAdapter = typeAdapter;
                  return gsonBuilder;
                }))
        .when(gsonBuilder)
        .registerTypeAdapter(eq(ResultsContainer.class), any());

    when(gsonBuilder.create()).thenReturn(gson);
    resultsContainer.AxeResult = axeResultMock;
    resultsContainer.ATFAResults = atfaResults;
    testSubject = new ResultSerializer(atfaResultsSerializer, gsonBuilder);
  }

  @Test
  public void generatesExpectedJson() {
    AtomicReference<ResultsContainer> resultsContainer = new AtomicReference<>();
    doAnswer(
            AdditionalAnswers.answer(
                (ResultsContainer container) -> {
                  resultsContainer.set(container);
                  return "Test String";
                }))
        .when(gson)
        .toJson(any(ResultsContainer.class));

    testSubject.createResultsJson(axeResultMock, atfaResults);

    Assert.assertEquals(axeResultMock, resultsContainer.get().AxeResult);
    Assert.assertEquals(atfaResults, resultsContainer.get().ATFAResults);
  }

  @Test
  public void typeAdapterSerializes() throws IOException {
    String axeJson = "axe scan result";
    String atfaJson = "atfa scan results";

    when(axeResultMock.toJson()).thenReturn(axeJson);
    when(atfaResultsSerializer.serializeATFAResults(atfaResults)).thenReturn(atfaJson);
    when(jsonWriter.name("AxeResults")).thenReturn(jsonWriter);
    when(jsonWriter.name("ATFAResults")).thenReturn(jsonWriter);

    resultsContainerTypeAdapter.write(jsonWriter, resultsContainer);

    verify(jsonWriter, times(1)).beginObject();
    verify(jsonWriter, times(1)).jsonValue(axeJson);
    verify(jsonWriter, times(1)).jsonValue(atfaJson);
    verify(jsonWriter, times(1)).endObject();
    verify(axeResultMock, times(1)).toJson();
  }
}
