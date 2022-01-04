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

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({GsonBuilder.class, Gson.class})
public class ResultsV2ContainerSerializerTest {

  @Mock AxeResult axeResultMock;
  @Mock ATFARulesSerializer atfaRulesSerializer;
  @Mock ATFAResultsSerializer atfaResultsSerializer;
  @Mock GsonBuilder gsonBuilder;
  @Mock JsonWriter jsonWriter;
  @Mock Gson gson;

  final List<AccessibilityHierarchyCheckResult> atfaResults = Collections.emptyList();
  final ResultsV2Container resultsV2Container = new ResultsV2Container();

  TypeAdapter<ResultsV2Container> resultsContainerTypeAdapter;
  ResultsV2ContainerSerializer testSubject;

  @Before
  public void prepare() {
    doAnswer(
            AdditionalAnswers.answer(
                (Type type, TypeAdapter<ResultsV2Container> typeAdapter) -> {
                  resultsContainerTypeAdapter = typeAdapter;
                  return gsonBuilder;
                }))
        .when(gsonBuilder)
        .registerTypeAdapter(eq(ResultsV2Container.class), any());

    when(gsonBuilder.create()).thenReturn(gson);
    resultsV2Container.AxeResult = axeResultMock;
    resultsV2Container.ATFAResults = atfaResults;
    testSubject =
        new ResultsV2ContainerSerializer(atfaRulesSerializer, atfaResultsSerializer, gsonBuilder);
  }

  @Test
  public void generatesExpectedJson() {
    AtomicReference<ResultsV2Container> resultsContainer = new AtomicReference<>();
    doAnswer(
            AdditionalAnswers.answer(
                (ResultsV2Container container) -> {
                  resultsContainer.set(container);
                  return "Test String";
                }))
        .when(gson)
        .toJson(any(ResultsV2Container.class));

    testSubject.createResultsJson(axeResultMock, atfaResults);

    Assert.assertEquals(axeResultMock, resultsContainer.get().AxeResult);
    Assert.assertEquals(atfaResults, resultsContainer.get().ATFAResults);
  }

  @Test
  public void typeAdapterSerializes() throws IOException {
    String axeJson = "axe scan result";
    String atfaRulesJson = "atfa rules";
    String atfaJson = "atfa scan results";

    when(axeResultMock.toJson()).thenReturn(axeJson);
    when(atfaRulesSerializer.serializeATFARules()).thenReturn(atfaRulesJson);
    when(atfaResultsSerializer.serializeATFAResults(atfaResults)).thenReturn(atfaJson);
    when(jsonWriter.name("AxeResults")).thenReturn(jsonWriter);
    when(jsonWriter.name("ATFARules")).thenReturn(jsonWriter);
    when(jsonWriter.name("ATFAResults")).thenReturn(jsonWriter);

    resultsContainerTypeAdapter.write(jsonWriter, resultsV2Container);

    verify(jsonWriter, times(1)).beginObject();
    verify(jsonWriter, times(1)).jsonValue(axeJson);
    verify(jsonWriter, times(1)).jsonValue(atfaRulesJson);
    verify(jsonWriter, times(1)).jsonValue(atfaJson);
    verify(jsonWriter, times(1)).endObject();
    verify(axeResultMock, times(1)).toJson();
  }
}
