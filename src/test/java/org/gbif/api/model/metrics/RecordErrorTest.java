/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.model.metrics;

import org.gbif.api.vocabulary.ProcessingErrorType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Deprecated
public class RecordErrorTest {

  /**
   * Test that the inherited builder operates.
   */
  @Test
  public void testBuilder() {
    RecordError recordError = RecordError.builder()
      .catalogNumber("1000")
      .collectionCode("CC700")
      .errorType(ProcessingErrorType.MISSING_BASIS_OF_RECORD)
      .institutionCode("GBIF")
      .recordId(12345L)
      .build();

    RecordError controlRE = new RecordError();
    controlRE.setCatalogNumber("1000");
    controlRE.setCollectionCode("CC700");
    controlRE.setProcessingErrorType(ProcessingErrorType.MISSING_BASIS_OF_RECORD);
    controlRE.setInstitutionCode("GBIF");
    controlRE.setRecordId(12345L);

    assertEquals(recordError, controlRE);
  }

}
