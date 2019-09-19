/*
 * Copyright 2014 Global Biodiversity Information Facility (GBIF)
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
package org.gbif.api.model.occurrence.predicate;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import static org.codehaus.jackson.annotate.JsonSubTypes.Type;

@com.fasterxml.jackson.annotation.JsonTypeInfo(
  use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
  property = "type"
)
@com.fasterxml.jackson.annotation.JsonSubTypes({
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConjunctionPredicate.class, name = "and"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DisjunctionPredicate.class, name = "or"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = EqualsPredicate.class, name = "equals"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = LikePredicate.class, name = "like"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = LessThanPredicate.class, name = "lessThan"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = LessThanOrEqualsPredicate.class, name = "lessThanOrEquals"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = GreaterThanPredicate.class, name = "greaterThan"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = GreaterThanOrEqualsPredicate.class, name = "greaterThanOrEquals"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = InPredicate.class, name = "in"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = WithinPredicate.class, name = "within"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = NotPredicate.class, name = "not"),
  @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = IsNotNullPredicate.class, name = "isNotNull")
})
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({@Type(value = ConjunctionPredicate.class, name = "and"),
               @Type(value = DisjunctionPredicate.class, name = "or"),
               @Type(value = EqualsPredicate.class, name = "equals"),
               @Type(value = LikePredicate.class, name = "like"),
               @Type(value = LessThanPredicate.class, name = "lessThan"),
               @Type(value = LessThanOrEqualsPredicate.class, name = "lessThanOrEquals"),
               @Type(value = GreaterThanPredicate.class, name = "greaterThan"),
               @Type(value = GreaterThanOrEqualsPredicate.class, name = "greaterThanOrEquals"),
               @Type(value = InPredicate.class, name = "in"),
               @Type(value = WithinPredicate.class, name = "within"),
               @Type(value = NotPredicate.class, name = "not"),
               @Type(value = IsNotNullPredicate.class, name = "isNotNull")})
public interface Predicate {
}
