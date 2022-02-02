// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.when;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.Parameters;
import com.google.android.apps.common.testing.accessibility.framework.ResultMetadata;
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchy;
import com.google.android.apps.common.testing.accessibility.framework.uielement.ViewHierarchyElement;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ATFARulesSerializerTest {
  MockedStatic<AccessibilityCheckPreset> accessibilityCheckPresetStaticMock;

  ATFARulesSerializer testSubject;

  class TestCheckClass extends AccessibilityHierarchyCheck {
    private static final String ANDROID_A11Y_HELP_URL = "excluded from serialized rule";
    private static final String TEST_RESULT_ID = "test result id included in serialized rule";
    private final Pattern TEST_PATTERN_TO_BE_SKIPPED = Pattern.compile("");

    @Override
    protected String getHelpTopic() {
      return "test-help-topic";
    }

    @Override
    public Category getCategory() {
      return Category.IMPLEMENTATION;
    }

    @Override
    public String getTitleMessage(Locale locale) {
      return "test-title-message";
    }

    @Override
    public String getMessageForResultData(Locale locale, int i, ResultMetadata resultMetadata) {
      return null;
    }

    @Override
    public String getShortMessageForResultData(
        Locale locale, int i, ResultMetadata resultMetadata) {
      return null;
    }

    @Override
    public List<AccessibilityHierarchyCheckResult> runCheckOnHierarchy(
        AccessibilityHierarchy accessibilityHierarchy,
        ViewHierarchyElement viewHierarchyElement,
        Parameters parameters) {
      return null;
    }
  }

  @Before
  public void prepare() {
    accessibilityCheckPresetStaticMock = Mockito.mockStatic(AccessibilityCheckPreset.class);
    testSubject = new ATFARulesSerializer();
  }

  @After
  public void cleanUp() {
    accessibilityCheckPresetStaticMock.close();
  }

  @Test
  public void serializeATFARulesReturnsExpectedRules() {
    TestCheckClass checkStub = new TestCheckClass();

    String expectedSerializedRules =
        "[\n"
            + "  {\n"
            + "    \"class\": \"com.microsoft.accessibilityinsightsforandroidservice.ATFARulesSerializerTest$TestCheckClass\",\n"
            + "    \"titleMessage\": \"test-title-message\",\n"
            + "    \"category\": \"IMPLEMENTATION\",\n"
            + "    \"helpUrl\": \"https://support.google.com/accessibility/android/answer/test-help-topic\",\n"
            + "    \"resultIdsAndMetadata\": {\n"
            + "       \"TEST_RESULT_ID\": \"test result id included in serialized rule\"\n"
            + "      }\n"
            + "  }\n"
            + "]";

    accessibilityCheckPresetStaticMock.when(() -> AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(
            AccessibilityCheckPreset.LATEST))
        .thenReturn(ImmutableSet.of(checkStub));

    String actualSerializedRules = testSubject.serializeATFARules();

    Assert.assertEquals(
        JsonParser.parseString(expectedSerializedRules),
        JsonParser.parseString(actualSerializedRules));
  }
}
