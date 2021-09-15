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
package org.gbif.api.model.collections.request;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.IdentifierType;
import org.gbif.api.vocabulary.collections.AccessionStatus;
import org.gbif.api.vocabulary.collections.CollectionContentType;
import org.gbif.api.vocabulary.collections.PreservationType;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CollectionSearchRequest extends SearchRequest {

  @Nullable private UUID institution;
  @Nullable private List<CollectionContentType> contentTypes;
  @Nullable private List<PreservationType> preservationTypes;
  @Nullable private AccessionStatus accessionStatus;
  @Nullable private Boolean personalCollection;

  @Nullable
  public UUID getInstitution() {
    return institution;
  }

  public void setInstitution(@Nullable UUID institution) {
    this.institution = institution;
  }

  @Nullable
  public List<CollectionContentType> getContentTypes() {
    return contentTypes;
  }

  public void setContentTypes(@Nullable List<CollectionContentType> contentTypes) {
    this.contentTypes = contentTypes;
  }

  @Nullable
  public List<PreservationType> getPreservationTypes() {
    return preservationTypes;
  }

  public void setPreservationTypes(@Nullable List<PreservationType> preservationTypes) {
    this.preservationTypes = preservationTypes;
  }

  @Nullable
  public AccessionStatus getAccessionStatus() {
    return accessionStatus;
  }

  public void setAccessionStatus(@Nullable AccessionStatus accessionStatus) {
    this.accessionStatus = accessionStatus;
  }

  @Nullable
  public Boolean getPersonalCollection() {
    return personalCollection;
  }

  public void setPersonalCollection(@Nullable Boolean personalCollection) {
    this.personalCollection = personalCollection;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    UUID institution;
    UUID contact;
    String q;
    String code;
    String name;
    String alternativeCode;
    String machineTagNamespace;
    String machineTagName;
    String machineTagValue;
    IdentifierType identifierType;
    String identifier;
    Country country;
    String city;
    String fuzzyName;
    Boolean active;
    List<CollectionContentType> contentTypes;
    List<PreservationType> preservationTypes;
    AccessionStatus accessionStatus;
    Boolean personalCollection;
    Pageable page;

    public Builder institution(UUID institution) {
      this.institution = institution;
      return this;
    }

    public Builder contact(UUID contact) {
      this.contact = contact;
      return this;
    }

    public Builder query(String q) {
      this.q = q;
      return this;
    }

    public Builder code(String code) {
      this.code = code;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder alternativeCode(String alternativeCode) {
      this.alternativeCode = alternativeCode;
      return this;
    }

    public Builder machineTagNamespace(String machineTagNamespace) {
      this.machineTagNamespace = machineTagNamespace;
      return this;
    }

    public Builder machineTagName(String machineTagName) {
      this.machineTagName = machineTagName;
      return this;
    }

    public Builder machineTagValue(String machineTagValue) {
      this.machineTagValue = machineTagValue;
      return this;
    }

    public Builder identifierType(IdentifierType identifierType) {
      this.identifierType = identifierType;
      return this;
    }

    public Builder identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder country(Country country) {
      this.country = country;
      return this;
    }

    public Builder city(String city) {
      this.city = city;
      return this;
    }

    public Builder fuzzyName(String fuzzyName) {
      this.fuzzyName = fuzzyName;
      return this;
    }

    public Builder active(boolean active) {
      this.active = active;
      return this;
    }

    public Builder contentTypes(List<CollectionContentType> contentTypes) {
      this.contentTypes = contentTypes;
      return this;
    }

    public Builder preservationTypes(List<PreservationType> preservationTypes) {
      this.preservationTypes = preservationTypes;
      return this;
    }

    public Builder accessionStatus(AccessionStatus accessionStatus) {
      this.accessionStatus = accessionStatus;
      return this;
    }

    public Builder personalCollection(boolean personalCollection) {
      this.personalCollection = personalCollection;
      return this;
    }

    public Builder page(Pageable page) {
      this.page = page;
      return this;
    }

    public CollectionSearchRequest build() {
      CollectionSearchRequest req = new CollectionSearchRequest();
      req.setInstitution(institution);
      req.setContact(contact);
      req.setQ(q);
      req.setCode(code);
      req.setName(name);
      req.setAlternativeCode(alternativeCode);
      req.setMachineTagNamespace(machineTagNamespace);
      req.setMachineTagName(machineTagName);
      req.setMachineTagValue(machineTagValue);
      req.setIdentifierType(identifierType);
      req.setIdentifier(identifier);
      req.setCountry(country);
      req.setCity(city);
      req.setFuzzyName(fuzzyName);
      req.setActive(active);
      req.setContentTypes(contentTypes);
      req.setPreservationTypes(preservationTypes);
      req.setAccessionStatus(accessionStatus);
      req.setPersonalCollection(personalCollection);
      if (page != null) {
        req.setLimit(page.getLimit());
        req.setOffset(page.getOffset());
      }
      return req;
    }
  }
}
