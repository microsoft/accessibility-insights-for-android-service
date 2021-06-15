// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.deque.axe.android.AxeResult;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.List;

public class ResultsContainerSerializer {
  private final ATFARulesSerializer atfaRulesSerializer;
  private final ATFAResultsSerializer atfaResultsSerializer;
  private final Gson gson;
  private final TypeAdapter<ResultsContainer> resultsContainerTypeAdapter =
      new TypeAdapter<ResultsContainer>() {
        @Override
        public void write(JsonWriter out, ResultsContainer value) throws IOException {
          out.beginObject();
          out.name("AxeResults").jsonValue(value.AxeResult.toJson());
          out.name("ATFARules").jsonValue(atfaRulesSerializer.serializeATFARules());
          out.name("ATFAResults")
              .jsonValue(atfaResultsSerializer.serializeATFAResults(value.ATFAResults));
          out.endObject();
        }

        @Override
        public ResultsContainer read(JsonReader in) {
          return null;
        }
      };

  public ResultsContainerSerializer(
      ATFAResultsSerializer atfaResultsSerializer, GsonBuilder gsonBuilder) {
    this.atfaResultsSerializer = atfaResultsSerializer;
    this.gson =
        gsonBuilder
            .registerTypeAdapter(ResultsContainer.class, this.resultsContainerTypeAdapter)
            .create();
    this.atfaRulesSerializer = new ATFARulesSerializer();
  }

  public String createResultsJson(
      AxeResult axeResult, List<AccessibilityHierarchyCheckResult> atfaResults) {
    ResultsContainer container = new ResultsContainer();
    container.ATFAResults = atfaResults;
    container.AxeResult = axeResult;
    return gson.toJson(container);
  }
}
