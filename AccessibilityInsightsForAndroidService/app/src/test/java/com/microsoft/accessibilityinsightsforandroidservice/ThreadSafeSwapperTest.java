// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
public class ThreadSafeSwapperTest {
  @Mock GenericTestObject mockOldObject;

  @Mock GenericTestObject mockIntermediateObject;

  @Mock GenericTestObject mockNewObject;

  ThreadSafeSwapper<GenericTestObject> testSubject;

  @Before
  public void prepare() {
    testSubject = new ThreadSafeSwapper<>();
  }

  @Test
  public void threadSafeSwapperExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void swapReturnsSwappedOutObject() {
    Whitebox.setInternalState(testSubject, "currentObject", mockOldObject);

    GenericTestObject actualReturnedObject = testSubject.swap(mockNewObject);

    Assert.assertEquals(mockOldObject, actualReturnedObject);
  }

  @Test
  public void swapReplacesCurrentObjectWithMethodParameter() {
    Whitebox.setInternalState(testSubject, "currentObject", mockOldObject);

    GenericTestObject actualOldObject = testSubject.swap(mockIntermediateObject);
    GenericTestObject actualIntermediateObject = testSubject.swap(mockNewObject);

    Assert.assertEquals(mockOldObject, actualOldObject);
    Assert.assertEquals(mockIntermediateObject, actualIntermediateObject);
  }

  @Test
  public void setIfCurrentlyNullDoesNotSetCurrentObjectIfNotNull() {
    boolean expectedReturnValue = false;
    Whitebox.setInternalState(testSubject, "currentObject", mockOldObject);

    boolean actualReturnValue = testSubject.setIfCurrentlyNull(mockNewObject);

    Assert.assertEquals(expectedReturnValue, actualReturnValue);
  }

  @Test
  public void setIfCurrentlyNullSetsCurrentObjectIfNull() {
    boolean expectedReturnValue = true;
    Whitebox.setInternalState(testSubject, "currentObject", (GenericTestObject) null);

    boolean actualReturnValue = testSubject.setIfCurrentlyNull(mockNewObject);

    Assert.assertEquals(expectedReturnValue, actualReturnValue);
  }

  private class GenericTestObject {}
}
