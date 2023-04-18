/*
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
package org.gbif.api.model.occurrence;

import org.gbif.api.annotation.Experimental;
import org.gbif.api.model.common.Identifier;
import org.gbif.api.model.common.LinneanClassification;
import org.gbif.api.model.common.LinneanClassificationKeys;
import org.gbif.api.model.common.MediaObject;
import org.gbif.api.util.ClassificationUtils;
import org.gbif.api.vocabulary.BasisOfRecord;
import org.gbif.api.vocabulary.Continent;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.License;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.api.vocabulary.OccurrenceStatus;
import org.gbif.api.vocabulary.Rank;
import org.gbif.api.vocabulary.Sex;
import org.gbif.api.vocabulary.TaxonomicStatus;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.UnknownTerm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents an Occurrence as interpreted by GBIF, adding typed properties on top of the verbatim ones.
 */
@SuppressWarnings("unused")
public class Occurrence extends VerbatimOccurrence implements LinneanClassification, LinneanClassificationKeys {

  public static final String GEO_DATUM = "WGS84";
  // keep names of ALL properties of this class in a set for jackson serialization, see #properties()
  private static final Set<String> PROPERTIES = Collections.unmodifiableSet(
    Stream.concat(
      // we need to these JSON properties manually because we have a fixed getter but no field for it
      Stream.of(DwcTerm.geodeticDatum.simpleName(), "class", "countryCode"),
      Stream.concat(Arrays.stream(Occurrence.class.getDeclaredFields()),
        Arrays.stream(VerbatimOccurrence.class.getDeclaredFields()))
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .map(Field::getName)).collect(Collectors.toSet()));

  // occurrence fields

  // OpenAPI documentation comes from the enumeration.
  private BasisOfRecord basisOfRecord;

  @Schema(
    description = "The number of individuals present at the time of the Occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/individualCount"
    )
  )
  private Integer individualCount;

  // OpenAPI documentation comes from the enumeration.
  private OccurrenceStatus occurrenceStatus;

  // OpenAPI documentation comes from the enumeration.
  private Sex sex;

  @Schema(
    description = "The age class or life stage of the Organism(s) at the time the Occurrence was recorded.\n\n" +
      "Values are aligned to the [GBIF LifeStage vocabulary](https://registry.gbif.org/vocabulary/LifeStage/concepts)",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/lifeStage"
    )
  )
  private String lifeStage;

  @Schema(
    description = "Statement about whether an organism or organisms have been introduced to a given place and time " +
      "through the direct or indirect activity of modern humans.\n\n" +
      "Values are aligned to the [GBIF EstablishmentMeans vocabulary](https://registry.gbif.org/vocabulary/EstablishmentMeans/concepts)," +
      "which is derived from the [Darwin Core EstablishmentMeans vocabulary](https://dwc.tdwg.org/em/).",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/establishmentMeans"
    )
  )
  private String establishmentMeans;

  @Schema(
    description = "The degree to which an Organism survives, reproduces, and expands its range at the given " +
      "place and time.\n\n" +
      "Values are aligned to the [GBIF DegreeOfEstablishment vocabulary](https://registry.gbif.org/vocabulary/DegreeOfEstablishment/concepts)," +
      "which is derived from the [Darwin Core DegreeOfEstablishment vocabulary](https://dwc.tdwg.org/doe/).",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/degreeOfEstablishment"
    )
  )
  private String degreeOfEstablishment;

  @Schema(
    description = "The process by which an Organism came to be in a given place at a given time.\n\n" +
    "Values are aligned to the [GBIF Pathway vocabulary](https://registry.gbif.org/vocabulary/Pathway/concepts)," +
    "which is derived from the [Darwin Core Pathway vocabulary](https://dwc.tdwg.org/pw/).",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/pathway"
    )
  )
  private String pathway;

  // taxonomy as NUB keys → LinneanClassificationKeys

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the most specific " +
      "(lowest rank) taxon for this occurrence.  This could be a synonym, see `acceptedTaxonKey`.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer taxonKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the kingdom of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer kingdomKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the phylum of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer phylumKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the class of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer classKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the order of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer orderKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the family of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer familyKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the genus of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer genusKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the subgenus of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer subgenusKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the species of this" +
      "occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer speciesKey;

  @Schema(
    description = "A taxon key from the [GBIF backbone](https://doi.org/10.15468/39omei) for the accepted taxon of " +
      "this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private Integer acceptedTaxonKey;

  // taxonomy as name strings → LinneanClassification

  @Schema(
    description = "The scientific name (including authorship) for the taxon from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.  This could be a synonym, see " +
      "also `acceptedScientificName`.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String scientificName;  // the interpreted name matching taxonKey

  @Schema(
    description = "The accepted scientific name (including authorship) for the taxon from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String acceptedScientificName;

  @Schema(
    description = "The kingdom name (excluding authorship) for the kingdom from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String kingdom;

  @Schema(
    description = "The phylum name (excluding authorship) for the phylum from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String phylum;

  @Schema(
    description = "The class name (excluding authorship) for the class from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  @JsonProperty("class")
  private String clazz;

  @Schema(
    description = "The order name (excluding authorship) for the order from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String order;

  @Schema(
    description = "The family name (excluding authorship) for the family from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String family;

  @Schema(
    description = "The genus name (excluding authorship) for the genus from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String genus;

  @Schema(
    description = "The subgenus name (excluding authorship) for the subgenus from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String subgenus;

  @Schema(
    description = "The species name (excluding authorship) for the species from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String species;

  // atomised scientific name

  @Schema(
    description = "The genus name part of the species name from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/genericName"
    )
  )
  private String genericName;

  @Schema(
    description = "The specific name part of the species name from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/specificEpithet"
    )
  )
  private String specificEpithet;

  @Schema(
    description = "The infraspecific name part of the species name from the " +
      "[GBIF backbone](https://doi.org/10.15468/39omei) matched to this occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/infraspecificEpithet"
    )
  )
  private String infraspecificEpithet;

  @Schema(
    description = "The taxonomic rank of the most specific name in the scientificName.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/taxonRank"
    )
  )
  private Rank taxonRank;

  @Schema(
    description = "The status of the use of the scientificName as a label for a taxon.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/taxonomicStatus"
    )
  )
  private TaxonomicStatus taxonomicStatus;

  @Schema(
    description = "The IUCN Red List Category of the taxon of this occurrence.\n\n" +
      "See the [GBIF vocabulary](https://rs.gbif.org/vocabulary/iucn/threat_status/) for the values and their " +
      "definitions, and the [IUCN Red List of Threatened Species dataset in GBIF](https://doi.org/10.15468/0qnb58) " +
      "for the version of the Red List GBIF's interpretation procedures are using.",
    externalDocs = @ExternalDocumentation(
      description = "GBIF vocabulary",
      url = "https://rs.gbif.org/vocabulary/iucn/threat_status/"
    )
  )
  private String iucnRedListCategory;

  // identification

  @Schema(
    description = "The date on which the subject was determined as representing the Taxon.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/dateIdentified"
    )
  )
  private Date dateIdentified;

  // location

  @Schema(
    description = "The geographic latitude (in decimal degrees, using the WGS84 datum) of the geographic centre " +
      "of the location of the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/decimalLatitude"
    )
  )
  private Double decimalLatitude;

  @Schema(
    description = "The geographic longitude (in decimal degrees, using the WGS84 datum) of the geographic centre " +
      "of the location of the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/decimalLongitude"
    )
  )
  private Double decimalLongitude;

  //coordinatePrecision and coordinateUncertaintyInMeters should be BigDecimal see POR-2795

  @Schema(
    description = "A decimal representation of the precision of the coordinates given in the decimalLatitude and decimalLongitude.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/coordinatePrecision"
    )
  )
  private Double coordinatePrecision;

  @Schema(
    description = "The horizontal distance (in metres) from the given decimalLatitude and decimalLongitude " +
      "describing the smallest circle containing the whole of the Location.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/coordinateUncertaintyInMeters"
    )
  )
  private Double coordinateUncertaintyInMeters;

  @Schema(
    description = "**Deprecated.**  This value is always null.  It is an obsolete Darwin Core term.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  @Deprecated //see getter
  private Double coordinateAccuracy;

  @Schema(
    description = "Elevation (altitude) in metres above sea level.  This is not a current Darwin Core term."
  )
  private Double elevation;

  @Schema(
    description = "The value of the potential error associated with the elevation.  This is not a current Darwin Core term."
  )
  private Double elevationAccuracy;

  @Schema(
    description = "Depth in metres below sea level.  This is not a current Darwin Core term."
  )
  private Double depth;

  @Schema(
    description = "The value of the potential error associated with the depth.  This is not a current Darwin Core term."
  )
  private Double depthAccuracy;

  // OpenAPI documentation from enumeration
  private Continent continent;

  @JsonSerialize(using = Country.IsoSerializer.class)
  @JsonDeserialize(using = Country.IsoDeserializer.class)
  private Country country;

  @Schema(
    description = "The name of the next-smaller administrative region than country (state, province, canton, " +
      "department, region, etc.) in which the occurrence occurs.\n\n" +
      "This value is unaltered by GBIF's processing; see also the GADM fields.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/stateProvince"
    )
  )
  private String stateProvince;

  @Schema(
    description = "The administrative divisions according to the [GADM database](https://gadm.org/).\n\n" +
      "This value is applied by GBIF's processing without consideration of the `stateProvince`, `county` or `locality` fields."
  )
  private Gadm gadm = new Gadm();

  @Schema(
    description = "The name of the water body in which the Location occurs.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/waterBody"
    )
  )
  private String waterBody;

  @Schema(
    description = "The distance in metres of the occurrence from a centroid known to be applied to occurrences " +
      "during georeferencing.  This can potentially indicate low-precision georeferencing, check the values of " +
      "`coordinateUncertaintyInMeters` and `georeferenceRemarks`."
  )
  private Double distanceFromCentroidInMeters;

  // recording event

  @Schema(
    description = "The four-digit year in which the event occurred, according to the Common Era calendar.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/year"
    )
  )
  private Integer year;

  @Schema(
    description = "The integer month in which the Event occurred.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/month"
    )
  )
  private Integer month;

  @Schema(
    description = "The integer day of the month on which the Event occurred.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/day"
    )
  )
  private Integer day;

  @Schema(
    description = "The date-time during which an Event occurred. For occurrences, this is the date-time when the " +
      "event was recorded. Not suitable for a time in a geological context.\n\n" +
      "**Note: This field is planned to expand to allow date ranges. See [issue](https://github.com/gbif/gbif-api/issues/4#issuecomment-1385497157).**",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/eventDate"
    )
  )
  private Date eventDate;

  @Schema(
    description = "A list (concatenated and separated) of nomenclatural types (type status, typified scientific name, " +
      "publication) applied to the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/typeStatus"
    )
  )
  private String typeStatus;

  // extracted from type status, but we should propose a new dwc term for this!
  // for example: "Paratype of Taeniopteryx metequi Ricker & Ross" is status=Paratype, typifiedName=Taeniopteryx metequi Ricker & Ross
  @Schema(
    description = "The scientific name that is based on the type specimen.\n\n" +
      "This is not yet a Darwin Core term, see the [proposal to add it](https://github.com/tdwg/dwc/issues/28)."
  )
  private String typifiedName; // missing from DwC

  @Schema(
    description = "A specific interpretation issue found during processing and interpretation of the record.\n\n" +
      "See the link:/en/guides/dev/issues_and_flags.html[list of occurrence issues] and the " +
      "https://gbif.github.io/gbif-api/apidocs/org/gbif/api/vocabulary/OccurrenceIssue.html[OccurrenceIssue enumeration] " +
      "for possible values and definitions."
  )
  private Set<OccurrenceIssue> issues = EnumSet.noneOf(OccurrenceIssue.class);

  // record level

  @Schema(
    description = "The most recent date-time on which the occurrence was changed, according to the publisher.",
    externalDocs = @ExternalDocumentation(
      description = "Dublin Core definition",
      url = "https://purl.org/dc/terms/modified"
    )
  )
  private Date modified;  // interpreted dc:modified, i.e. date changed in source

  @Schema(
    description = "The time this occurrence was last processed by GBIF's interpretation system “Pipelines”.\n\n" +
      "This is the time the record was last changed in GBIF, **not** the time the record was last changed by the " +
      "publisher.  Data is also reprocessed when we changed the taxonomic backbone, geographic data sources or " +
      "other interpretation procedures.\n\n" +
      "An earlier interpretation system distinguished between “parsing” and “interpretation”, but in the current " +
      "system there is only one process — the two dates will always be the same."
  )
  private Date lastInterpreted;

  @Schema(
    description = "A related resource that is referenced, cited, or otherwise pointed to by the described resource.",
    externalDocs = @ExternalDocumentation(
      description = "Dublin Core definition",
      url = "https://purl.org/dc/terms/references"
    )
  )
  private URI references;

  @Schema(
    description = "A legal document giving official permission to do something with the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Dublin Core definition",
      url = "https://purl.org/dc/terms/license"
    )
  )
  private License license;

  @Schema(
    description = "A number or enumeration value for the quantity of organisms.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/organismQuantity"
    )
  )
  private Double organismQuantity;

  @Schema(
    description = "The type of quantification system used for the quantity of organisms.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/organismQuantityType"
    )
  )
  private String organismQuantityType;

  @Schema(
    description = "The unit of measurement of the size (time duration, length, area, or volume) of a sample in a sampling event.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/sampleSizeUnit"
    )
  )
  private String sampleSizeUnit;

  @Schema(
    description = "A numeric value for a measurement of the size (time duration, length, area, or volume) of a sample in a sampling event.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/sampleSizeValue"
    )
  )
  private Double sampleSizeValue;

  @Schema(
    description = "The relative measurement of the quantity of the organism (i.e. without absolute units)."
  )
  private Double relativeOrganismQuantity;

  // interpreted extension data

  @Schema(
    description = "Alternative identifiers for the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "GBIF Alternative Identifiers extension",
      url = "https://rs.gbif.org/terms/1.0/Identifier"
    )
  )
  private List<Identifier> identifiers = new ArrayList<>();

  @Schema(
    description = "Multimedia related to te occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "GBIF Multimedia extension",
      url = "https://rs.gbif.org/terms/1.0/Multimedia"
    )
  )
  private List<MediaObject> media = new ArrayList<>();

  @Schema(
    description = "Measurements or facts about the the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/MeasurementOrFact"
    )
  )
  private List<MeasurementOrFact> facts = new ArrayList<>();

  @Schema(
    description = "Relationships between occurrences.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/ResourceRelationship"
    )
  )
  private List<OccurrenceRelation> relations = new ArrayList<>();

  @Schema(
    description = "A list of the globally unique identifiers for the person, people, groups, or organizations " +
      "responsible for recording the original Occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/recordedByID"
    )
  )
  @JsonProperty("recordedByIDs")
  private List<AgentIdentifier> recordedByIds = new ArrayList<>();

  @Schema(
    description = "A list of the globally unique identifiers for the person, people, groups, or organizations " +
      "responsible for assigning the Taxon to the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/identifiedByID"
    )
  )
  @JsonProperty("identifiedByIDs")
  private List<AgentIdentifier> identifiedByIds = new ArrayList<>();

  @Schema(
    description = "**Experimental.**  The UUID of the institution holding the specimen occurrence, from GRSciColl."
  )
  @Experimental
  private String institutionKey;

  @Schema(
    description = "**Experimental.**  The UUID of the collection containing the specimen occurrence, from GRSciColl."
  )
  @Experimental
  private String collectionKey;

  @Schema(
    description = "**Experimental.**  Whether the occurrence belongs to a machine-calculated cluster of probable " +
      "duplicate occurrences.",
    externalDocs = @ExternalDocumentation(
      description = "GBIF Data Blog",
      url = "https://data-blog.gbif.org/post/clustering-occurrences/"
    )
  )
  @Experimental
  private boolean isInCluster;

  @Schema(
    description = "An identifier for the set of data. May be a global unique identifier or an identifier specific to " +
      "a collection or institution.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/datasetID"
    )
  )
  private String datasetID;

  @Schema(
    description = "The name identifying the data set from which the record was derived.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/"
    )
  )
  private String datasetName;

  @Schema(
    description = "A list (concatenated and separated) of previous or alternate fully qualified catalogue numbers " +
      "or other human-used identifiers for the same occurrence, whether in the current or any other data set or collection.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/otherCatalogNumbers"
    )
  )
  private String otherCatalogNumbers;

  @Schema(
    description = "A person, group, or organization responsible for recording the original occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/recordedBy"
    )
  )
  private String recordedBy;

  @Schema(
    description = "A list (concatenated and separated) of names of people, groups, or organizations who assigned the " +
      "Taxon to the occurrence.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/identifiedBy"
    )
  )
  private String identifiedBy;

  @Schema(
    description = "A preparation or preservation method for a specimen.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/preparations"
    )
  )
  private String preparations;

  @Schema(
    description = "The methods or protocols used during an Event, denoted by an IRI.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/samplingProtocol"
    )
  )
  private String samplingProtocol;

  public Occurrence() {

  }

  /**
   * Create occurrence instance from existing verbatim one, copying over all data.
   */
  public Occurrence(@Nullable VerbatimOccurrence verbatim) {
    if (verbatim != null) {
      setKey(verbatim.getKey());
      setDatasetKey(verbatim.getDatasetKey());
      setPublishingOrgKey(verbatim.getPublishingOrgKey());
      setPublishingCountry(verbatim.getPublishingCountry());
      setProtocol(verbatim.getProtocol());
      setCrawlId(verbatim.getCrawlId());
      if (verbatim.getLastCrawled() != null) {
        setLastCrawled(new Date(verbatim.getLastCrawled().getTime()));
      }
      if (verbatim.getVerbatimFields() != null) {
        getVerbatimFields().putAll(verbatim.getVerbatimFields());
      }
      if (verbatim.getLastParsed() != null) {
        setLastParsed(verbatim.getLastParsed());
      }
      setExtensions(verbatim.getExtensions());
    }
  }

  @Nullable
  public BasisOfRecord getBasisOfRecord() {
    return basisOfRecord;
  }

  public void setBasisOfRecord(BasisOfRecord basisOfRecord) {
    this.basisOfRecord = basisOfRecord;
  }

  @Nullable
  public Integer getIndividualCount() {
    return individualCount;
  }

  public void setIndividualCount(Integer individualCount) {
    this.individualCount = individualCount;
  }

  @Nullable
  public OccurrenceStatus getOccurrenceStatus() {
    return occurrenceStatus;
  }

  public void setOccurrenceStatus(OccurrenceStatus occurrenceStatus) {
    this.occurrenceStatus = occurrenceStatus;
  }

  @Nullable
  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  @Nullable
  public String getLifeStage() {
    return lifeStage;
  }

  public void setLifeStage(String lifeStage) {
    this.lifeStage = lifeStage;
  }

  @Nullable
  public String getEstablishmentMeans() {
    return establishmentMeans;
  }

  public void setEstablishmentMeans(String establishmentMeans) {
    this.establishmentMeans = establishmentMeans;
  }

  /**
   * The best matching, accepted GBIF backbone name usage representing this occurrence.
   * In case the verbatim scientific name and its classification can only be matched to a higher rank this will
   * represent the lowest matching rank. In the worst case this could just be for example Animalia.
   */
  @Nullable
  public Integer getTaxonKey() {
    return taxonKey;
  }

  public void setTaxonKey(Integer taxonKey) {
    this.taxonKey = taxonKey;
  }

  @Nullable
  @Override
  public Integer getKingdomKey() {
    return kingdomKey;
  }

  @Override
  public void setKingdomKey(@Nullable Integer kingdomKey) {
    this.kingdomKey = kingdomKey;
  }

  @Nullable
  @Override
  public Integer getPhylumKey() {
    return phylumKey;
  }

  @Override
  public void setPhylumKey(@Nullable Integer phylumKey) {
    this.phylumKey = phylumKey;
  }

  @Nullable
  @Override
  public Integer getClassKey() {
    return classKey;
  }

  @Override
  public void setClassKey(@Nullable Integer classKey) {
    this.classKey = classKey;
  }

  @Nullable
  @Override
  public Integer getOrderKey() {
    return orderKey;
  }

  @Override
  public void setOrderKey(@Nullable Integer orderKey) {
    this.orderKey = orderKey;
  }

  @Nullable
  @Override
  public Integer getFamilyKey() {
    return familyKey;
  }

  @Override
  public void setFamilyKey(@Nullable Integer familyKey) {
    this.familyKey = familyKey;
  }

  @Nullable
  @Override
  public Integer getGenusKey() {
    return genusKey;
  }

  @Override
  public void setGenusKey(@Nullable Integer genusKey) {
    this.genusKey = genusKey;
  }

  @Nullable
  @Override
  public Integer getSubgenusKey() {
    return subgenusKey;
  }

  @Override
  public void setSubgenusKey(@Nullable Integer subgenusKey) {
    this.subgenusKey = subgenusKey;
  }

  @Nullable
  @Override
  public Integer getHigherRankKey(Rank rank) {
    return ClassificationUtils.getHigherRankKey(this, rank);
  }

  /**
   * An ordered map with entries for all higher Linnean ranks excluding the taxonKey itself.
   * The map starts with the highest rank, e.g. the kingdom and maps the name usage key to its canonical name.
   *
   * @return map of higher ranks
   */
  @NotNull
  @JsonIgnore
  public Map<Integer, String> getHigherClassificationMap() {
    return taxonKey == null ? ClassificationUtils.getHigherClassificationMap(this)
      : ClassificationUtils.getHigherClassificationMap(this, taxonKey, null, null);
  }

  /**
   * The accepted species for this occurrence. In case the taxonKey is of a higher rank than species (e.g. genus)
   * speciesKey is null. In case taxonKey represents an infraspecific taxon the speciesKey points to the species
   * the infraspecies is classified as. In case of taxonKey being a species the speciesKey is the same.
   */
  @Nullable
  @Override
  public Integer getSpeciesKey() {
    return speciesKey;
  }

  @Override
  public void setSpeciesKey(@Nullable Integer speciesKey) {
    this.speciesKey = speciesKey;
  }

  /**
   * The accepted taxon key from the GBIF backbone.
   */
  @Nullable
  public Integer getAcceptedTaxonKey() {
    return acceptedTaxonKey;
  }

  public void setAcceptedTaxonKey(Integer acceptedTaxonKey) {
    this.acceptedTaxonKey = acceptedTaxonKey;
  }

  @Nullable
  public String getSpecificEpithet() {
    return specificEpithet;
  }

  public void setSpecificEpithet(String specificEpithet) {
    this.specificEpithet = specificEpithet;
  }

  @Nullable
  public String getInfraspecificEpithet() {
    return infraspecificEpithet;
  }

  public void setInfraspecificEpithet(String infraspecificEpithet) {
    this.infraspecificEpithet = infraspecificEpithet;
  }

  @Nullable
  public Rank getTaxonRank() {
    return taxonRank;
  }

  public void setTaxonRank(Rank taxonRank) {
    this.taxonRank = taxonRank;
  }

  /**
   * The status of the use of the scientificName as a label for a taxon.
   * The GBIF recommended controlled value vocabulary can be found at <a href="http://rs.gbif.org/vocabulary/gbif/taxonomic_status.xml">http://rs.gbif.org/vocabulary/gbif/taxonomic_status.xm</a>.
   */
  @Nullable
  public TaxonomicStatus getTaxonomicStatus() {
    return taxonomicStatus;
  }

  public void setTaxonomicStatus(TaxonomicStatus taxonomicStatus) {
    this.taxonomicStatus = taxonomicStatus;
  }

  /**
   * The IUCN Red List Category.
   */
  @Nullable
  public String getIucnRedListCategory() {
    return iucnRedListCategory;
  }

  public void setIucnRedListCategory(String iucnRedListCategory) {
    this.iucnRedListCategory = iucnRedListCategory;
  }

  /**
   * The scientific name for taxonKey from the GBIF backbone.
   */
  @Nullable
  public String getScientificName() {
    return scientificName;
  }

  public void setScientificName(@Nullable String scientificName) {
    this.scientificName = scientificName;
  }

  /**
   * The verbatim scientific name as provided by the source.
   */
  @Nullable
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  public String getVerbatimScientificName() {
    return getVerbatimField(DwcTerm.scientificName);
  }

  public void setVerbatimScientificName(String scientificName) {
    //DO NOTHING
  }

  /**
   * The accepted scientific name for the acceptedTaxonKey from the GBIF backbone.
   */
  @Nullable
  public String getAcceptedScientificName() {
    return acceptedScientificName;
  }

  public void setAcceptedScientificName(String acceptedScientificName) {
    this.acceptedScientificName = acceptedScientificName;
  }

  @Nullable
  @Override
  public String getKingdom() {
    return kingdom;
  }

  @Override
  public void setKingdom(@Nullable String kingdom) {
    this.kingdom = kingdom;
  }

  @Nullable
  @Override
  public String getPhylum() {
    return phylum;
  }

  @Override
  public void setPhylum(@Nullable String phylum) {
    this.phylum = phylum;
  }

  @Nullable
  @Override
  public String getClazz() {
    return clazz;
  }

  @Override
  public void setClazz(@Nullable String clazz) {
    this.clazz = clazz;
  }

  @Nullable
  @Override
  public String getOrder() {
    return order;
  }

  @Override
  public void setOrder(@Nullable String order) {
    this.order = order;
  }

  @Nullable
  @Override
  public String getFamily() {
    return family;
  }

  @Override
  public void setFamily(@Nullable String family) {
    this.family = family;
  }

  @Nullable
  @Override
  public String getGenus() {
    return genus;
  }

  @Override
  public void setGenus(@Nullable String genus) {
    this.genus = genus;
  }

  @Nullable
  public String getGenericName() {
    return genericName;
  }

  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  @Nullable
  @Override
  public String getSubgenus() {
    return subgenus;
  }

  @Override
  public void setSubgenus(@Nullable String subgenus) {
    this.subgenus = subgenus;
  }

  @Nullable
  @Override
  public String getHigherRank(Rank rank) {
    return ClassificationUtils.getHigherRank(this, rank);
  }

  /**
   * The corresponding scientific name of the speciesKey from the GBIF backbone.
   */
  @Nullable
  @Override
  public String getSpecies() {
    return species;
  }

  @Override
  public void setSpecies(@Nullable String species) {
    this.species = species;
  }

  @Nullable
  public Date getDateIdentified() {
    return dateIdentified == null ? null : new Date(dateIdentified.getTime());
  }

  public void setDateIdentified(@Nullable Date dateIdentified) {
    this.dateIdentified = dateIdentified == null ? null : new Date(dateIdentified.getTime());
  }

  /**
   * The decimalLongitude in decimal degrees always for the WGS84 datum. If a different geodetic datum was given the verbatim
   * coordinates are transformed into WGS84 values.
   */
  @Nullable
  public Double getDecimalLongitude() {
    return decimalLongitude;
  }

  public void setDecimalLongitude(@Nullable Double decimalLongitude) {
    this.decimalLongitude = decimalLongitude;
  }

  @Nullable
  public Double getDecimalLatitude() {
    return decimalLatitude;
  }

  public void setDecimalLatitude(@Nullable Double decimalLatitude) {
    this.decimalLatitude = decimalLatitude;
  }

  /**
   * The uncertainty radius for lat/lon in meters.
   */
  @Nullable
  public Double getCoordinateUncertaintyInMeters() {
    return coordinateUncertaintyInMeters;
  }

  public void setCoordinateUncertaintyInMeters(@Nullable Double coordinateUncertaintyInMeters) {
    this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
  }

  @Nullable
  public Double getCoordinatePrecision() {
    return coordinatePrecision;
  }

  public void setCoordinatePrecision(Double coordinatePrecision) {
    this.coordinatePrecision = coordinatePrecision;
  }

  /**
   * @deprecated to be removed in the public v2 of the API (see POR-3061)
   * The uncertainty for latitude in decimal degrees.
   * Note that the longitude degrees have a different accuracy in degrees which changes with latitude and is largest at the poles.
   */
  @Nullable
  @Deprecated
  public Double getCoordinateAccuracy() {
    return coordinateAccuracy;
  }

  public void setCoordinateAccuracy(@Nullable Double coordinateAccuracy) {
    this.coordinateAccuracy = coordinateAccuracy;
  }

  /**
   * The geodetic datum for the interpreted decimal coordinates.
   * This is always WGS84 if a coordinate exists as we reproject other datums into WGS84.
   */
  @Schema(
    description = "The geodetic datum for the interpreted decimal coordinates.\n\n" +
      "Coordinates are reprojected to WGS84 if they exist, so this field is either null or `WGS84`."
  )
  @Nullable
  public String getGeodeticDatum() {
    if (decimalLatitude != null) {
      return GEO_DATUM;
    }
    return null;
  }

  /**
   * This private method is needed for jackson deserialization only.
   */
  private void setGeodeticDatum(String datum) {
    // ignore, we have a static WGS84 value
  }

  /**
   * Elevation in meters usually above sea level (altitude).
   * </br>
   * The elevation is calculated using the equation: (minimumElevationInMeters + maximumElevationInMeters) / 2.
   */
  @Nullable
  public Double getElevation() {
    return elevation;
  }

  public void setElevation(@Nullable Double elevation) {
    this.elevation = elevation;
  }

  /**
   * Elevation accuracy is the uncertainty for the elevation in meters.
   * </br>
   * The elevation accuracy is calculated using the equation: (maximumElevationInMeters - minimumElevationInMeters) / 2
   */
  @Nullable
  public Double getElevationAccuracy() {
    return elevationAccuracy;
  }

  public void setElevationAccuracy(@Nullable Double elevationAccuracy) {
    this.elevationAccuracy = elevationAccuracy;
  }

  /**
   * Depth in meters below the surface. Complimentary to elevation, the depth can be 10 meters below the surface of a
   * lake in 1100m (=elevation).
   * </br>
   * The depth is calculated using the equation: (minimumDepthInMeters + maximumDepthInMeters) / 2.
   */
  @Nullable
  public Double getDepth() {
    return depth;
  }

  public void setDepth(@Nullable Double depth) {
    this.depth = depth;
  }

  /**
   * Depth accuracy is the uncertainty for the depth in meters.
   * </br>
   * The depth accuracy is calculated using the equation: (maximumDepthInMeters - minimumDepthInMeters) / 2
   */
  @Nullable
  public Double getDepthAccuracy() {
    return depthAccuracy;
  }

  public void setDepthAccuracy(@Nullable Double depthAccuracy) {
    this.depthAccuracy = depthAccuracy;
  }

  @Nullable
  public Continent getContinent() {
    return continent;
  }

  public void setContinent(@Nullable Continent continent) {
    this.continent = continent;
  }

  @Schema(
    description = "The 2-letter country code (as per ISO-3166-1) of the country, territory or area in which the " +
      "occurrence was recorded.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/countryCode"
    )
  )
  @Nullable
  @JsonProperty("countryCode")
  public Country getCountry() {
    return country;
  }

  public void setCountry(@Nullable Country country) {
    this.country = country;
  }

  /**
   * Renders the country title as a JSON property country in addition to the ISO 3166 2 letter countryCode being
   * serialized by the regular country Java property.
   * Made private to use it only for JSON serialization and not within Java code.
   */
  @Schema(
    description = "The title (as per ISO-3166-1) of the country, territory or area in which the " +
      "occurrence was recorded.",
    externalDocs = @ExternalDocumentation(
      description = "Darwin Core definition",
      url = "https://rs.tdwg.org/dwc/terms/country"
    )
  )
  @Nullable
  @JsonProperty("country")
  private String getCountryTitle() {
    return country == null ? null : country.getTitle();
  }

  private void setCountryTitle(String country) {
    // ignore, setter only to avoid JSON being written into the fields map
  }

  @Nullable
  public String getStateProvince() {
    return stateProvince;
  }

  public void setStateProvince(@Nullable String stateProvince) {
    this.stateProvince = stateProvince;
  }

  @Nullable
  public String getWaterBody() {
    return waterBody;
  }

  public void setWaterBody(@Nullable String waterBody) {
    this.waterBody = waterBody;
  }

  /**
   * The distance in metres from a known centroid, e.g. a country centroid.
   */
  public Double getDistanceFromCentroidInMeters() {
    return distanceFromCentroidInMeters;
  }

  public void setDistanceFromCentroidInMeters(Double distanceFromCentroidInMeters) {
    this.distanceFromCentroidInMeters = distanceFromCentroidInMeters;
  }

  /**
   * The full year of the event date.
   *
   * @return the year of the event date
   */
  @Min(1500)
  @Max(2030)
  @Nullable
  public Integer getYear() {
    return year;
  }

  public void setYear(@Nullable Integer year) {
    this.year = year;
  }

  /**
   * The month of the year of the event date starting with zero for january following {@link Date}.
   *
   * @return the month of the event date
   */
  @Min(1)
  @Max(12)
  @Nullable
  public Integer getMonth() {
    return month;
  }

  public void setMonth(@Nullable Integer month) {
    this.month = month;
  }

  /**
   * The day of the month of the event date.
   *
   * @return the day of the event date
   */
  @Min(1)
  @Max(31)
  @Nullable
  public Integer getDay() {
    return day;
  }

  public void setDay(@Nullable Integer day) {
    this.day = day;
  }

  /**
   * The date the occurrence was recorded or collected.
   */
  @Nullable
  public Date getEventDate() {
    return eventDate == null ? null : new Date(eventDate.getTime());
  }

  public void setEventDate(@Nullable Date eventDate) {
    this.eventDate = eventDate == null ? null : new Date(eventDate.getTime());
  }

  @Nullable
  public String getTypeStatus() {
    return typeStatus;
  }

  public void setTypeStatus(@Nullable String typeStatus) {
    this.typeStatus = typeStatus;
  }

  /**
   * The scientific name the type status of this specimen applies to.
   */
  @Nullable
  public String getTypifiedName() {
    return typifiedName;
  }

  public void setTypifiedName(@Nullable String typifiedName) {
    this.typifiedName = typifiedName;
  }

  /**
   * A set of issues found for this occurrence.
   */
  @NotNull
  public Set<OccurrenceIssue> getIssues() {
    return issues;
  }

  public void setIssues(Set<OccurrenceIssue> issues) {
    Objects.requireNonNull(issues, "Issues cannot be null");
    EnumSet<OccurrenceIssue> set = EnumSet.noneOf(OccurrenceIssue.class);
    set.addAll(issues);
    this.issues = set;
  }

  public void addIssue(OccurrenceIssue issue) {
    Objects.requireNonNull(issue, "Issue needs to be specified");
    issues.add(issue);
  }

  /**
   * The interpreted dc:modified from the verbatim source data.
   * Ideally indicating when a record was last modified in the source.
   */
  @Nullable
  public Date getModified() {
    return modified == null ? null : new Date(modified.getTime());
  }

  public void setModified(@Nullable Date modified) {
    this.modified = modified == null ? null : new Date(modified.getTime());
  }

  /**
   * The date this occurrence last went through the interpretation phase of the GBIF indexing.
   */
  @Nullable
  public Date getLastInterpreted() {
    return lastInterpreted == null ? null : new Date(lastInterpreted.getTime());
  }

  public void setLastInterpreted(@Nullable Date lastInterpreted) {
    this.lastInterpreted = lastInterpreted == null ? null : new Date(lastInterpreted.getTime());
  }

  /**
   * An external link to more details, the records "homepage".
   */
  @Nullable
  public URI getReferences() {
    return references;
  }

  public void setReferences(URI references) {
    this.references = references;
  }

  /**
   * A number or enumeration value for the quantity of organisms.
   */
  @Nullable
  public Double getOrganismQuantity() {
    return organismQuantity;
  }

  public void setOrganismQuantity(@Nullable Double organismQuantity) {
    this.organismQuantity = organismQuantity;
  }

  /**
   * The type of quantification system used for the quantity of organisms.
   */
  @Nullable
  public String getOrganismQuantityType() {
    return organismQuantityType;
  }

  public void setOrganismQuantityType(@Nullable String organismQuantityType) {
    this.organismQuantityType = organismQuantityType;
  }

  /**
   * The unit of measurement of the size (time duration, length, area, or volume) of a sample in a sampling event.
   */
  @Nullable
  public String getSampleSizeUnit() {
    return sampleSizeUnit;
  }

  public void setSampleSizeUnit(@Nullable String sampleSizeUnit) {
    this.sampleSizeUnit = sampleSizeUnit;
  }

  /**
   * A numeric value for a measurement of the size (time duration, length, area, or volume) of a sample in a sampling event.
   */
  @Nullable
  public Double getSampleSizeValue() {
    return sampleSizeValue;
  }

  public void setSampleSizeValue(@Nullable Double sampleSizeValue) {
    this.sampleSizeValue = sampleSizeValue;
  }

  /**
   * Calculated filed organismQuantity / sampleSizeValue, if the type is identical
   */
  @Nullable
  public Double getRelativeOrganismQuantity() {
    return relativeOrganismQuantity;
  }

  public void setRelativeOrganismQuantity(@Nullable Double relativeOrganismQuantity) {
    this.relativeOrganismQuantity = relativeOrganismQuantity;
  }

  /**
   * Applied license to the occurrence record or dataset to which this record belongs to.
   */
  @NotNull
  public License getLicense() {
    return license;
  }

  public void setLicense(License license) {
    this.license = license;
  }

  @NotNull
  public List<Identifier> getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(List<Identifier> identifiers) {
    this.identifiers = identifiers;
  }

  @NotNull
  public List<MediaObject> getMedia() {
    return media;
  }

  public void setMedia(List<MediaObject> media) {
    this.media = media;
  }

  @NotNull
  public List<MeasurementOrFact> getFacts() {
    return facts;
  }

  public void setFacts(List<MeasurementOrFact> facts) {
    this.facts = facts;
  }

  @NotNull
  public List<OccurrenceRelation> getRelations() {
    return relations;
  }

  public void setRelations(List<OccurrenceRelation> relations) {
    this.relations = relations;
  }

  @NotNull
  public List<AgentIdentifier> getRecordedByIds() {
    return recordedByIds;
  }

  public void setRecordedByIds(List<AgentIdentifier> recordedByIds) {
    this.recordedByIds = recordedByIds;
  }

  @NotNull
  public List<AgentIdentifier> getIdentifiedByIds() {
    return identifiedByIds;
  }

  public void setIdentifiedByIds(List<AgentIdentifier> identifiedByIds) {
    this.identifiedByIds = identifiedByIds;
  }

  @NotNull
  public Gadm getGadm() {
    return gadm;
  }

  public void setGadm(Gadm gadm) {
    this.gadm = gadm;
  }

  @Nullable
  @Experimental
  public String getInstitutionKey() {
    return institutionKey;
  }

  public void setInstitutionKey(String institutionKey) {
    this.institutionKey = institutionKey;
  }

  @Nullable
  @Experimental
  public String getCollectionKey() {
    return collectionKey;
  }

  public void setCollectionKey(String collectionKey) {
    this.collectionKey = collectionKey;
  }

  public boolean getIsInCluster() {
    return isInCluster;
  }

  public void setIsInCluster(boolean isInCluster) {
    this.isInCluster = isInCluster;
  }

  @Nullable
  public String getDegreeOfEstablishment() {
    return degreeOfEstablishment;
  }

  public void setDegreeOfEstablishment(String degreeOfEstablishment) {
    this.degreeOfEstablishment = degreeOfEstablishment;
  }

  @Nullable
  public String getPathway() {
    return pathway;
  }

  public void setPathway(String pathway) {
    this.pathway = pathway;
  }

  public String getDatasetID() {
    return datasetID;
  }

  public void setDatasetID(String datasetID) {
    this.datasetID = datasetID;
  }

  public String getDatasetName() {
    return datasetName;
  }

  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  public String getOtherCatalogNumbers() {
    return otherCatalogNumbers;
  }

  public void setOtherCatalogNumbers(String otherCatalogNumbers) {
    this.otherCatalogNumbers = otherCatalogNumbers;
  }

  public String getRecordedBy() {
    return recordedBy;
  }

  public void setRecordedBy(String recordedBy) {
    this.recordedBy = recordedBy;
  }

  public String getIdentifiedBy() {
    return identifiedBy;
  }

  public void setIdentifiedBy(String identifiedBy) {
    this.identifiedBy = identifiedBy;
  }

  public String getPreparations() {
    return preparations;
  }

  public void setPreparations(String preparations) {
    this.preparations = preparations;
  }

  public String getSamplingProtocol() {
    return samplingProtocol;
  }

  public void setSamplingProtocol(String samplingProtocol) {
    this.samplingProtocol = samplingProtocol;
  }

  /**
   * Convenience method checking if any spatial validation rule has not passed.
   * Primarily used to indicate that the record should not be displayed on a map.
   */
  @JsonIgnore
  public boolean hasSpatialIssue() {
    for (OccurrenceIssue rule : OccurrenceIssue.GEOSPATIAL_RULES) {
      if (issues.contains(rule)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Occurrence that = (Occurrence) o;
    return basisOfRecord == that.basisOfRecord &&
      Objects.equals(individualCount, that.individualCount) &&
      sex == that.sex &&
      Objects.equals(lifeStage, that.lifeStage) &&
      Objects.equals(establishmentMeans, that.establishmentMeans) &&
      Objects.equals(taxonKey, that.taxonKey) &&
      Objects.equals(kingdomKey, that.kingdomKey) &&
      Objects.equals(phylumKey, that.phylumKey) &&
      Objects.equals(classKey, that.classKey) &&
      Objects.equals(orderKey, that.orderKey) &&
      Objects.equals(familyKey, that.familyKey) &&
      Objects.equals(genusKey, that.genusKey) &&
      Objects.equals(subgenusKey, that.subgenusKey) &&
      Objects.equals(speciesKey, that.speciesKey) &&
      Objects.equals(acceptedTaxonKey, that.acceptedTaxonKey) &&
      Objects.equals(scientificName, that.scientificName) &&
      Objects.equals(acceptedScientificName, that.acceptedScientificName) &&
      Objects.equals(kingdom, that.kingdom) &&
      Objects.equals(phylum, that.phylum) &&
      Objects.equals(clazz, that.clazz) &&
      Objects.equals(order, that.order) &&
      Objects.equals(family, that.family) &&
      Objects.equals(genus, that.genus) &&
      Objects.equals(subgenus, that.subgenus) &&
      Objects.equals(species, that.species) &&
      Objects.equals(genericName, that.genericName) &&
      Objects.equals(specificEpithet, that.specificEpithet) &&
      Objects.equals(infraspecificEpithet, that.infraspecificEpithet) &&
      taxonRank == that.taxonRank &&
      taxonomicStatus == that.taxonomicStatus &&
      Objects.equals(dateIdentified, that.dateIdentified) &&
      Objects.equals(decimalLongitude, that.decimalLongitude) &&
      Objects.equals(decimalLatitude, that.decimalLatitude) &&
      Objects.equals(coordinatePrecision, that.coordinatePrecision) &&
      Objects.equals(coordinateUncertaintyInMeters, that.coordinateUncertaintyInMeters) &&
      Objects.equals(elevation, that.elevation) &&
      Objects.equals(elevationAccuracy, that.elevationAccuracy) &&
      Objects.equals(depth, that.depth) &&
      Objects.equals(depthAccuracy, that.depthAccuracy) &&
      continent == that.continent &&
      country == that.country &&
      Objects.equals(stateProvince, that.stateProvince) &&
      Objects.equals(waterBody, that.waterBody) &&
      Objects.equals(year, that.year) &&
      Objects.equals(month, that.month) &&
      Objects.equals(day, that.day) &&
      Objects.equals(eventDate, that.eventDate) &&
      Objects.equals(typeStatus, that.typeStatus) &&
      Objects.equals(typifiedName, that.typifiedName) &&
      Objects.equals(issues, that.issues) &&
      Objects.equals(modified, that.modified) &&
      Objects.equals(lastInterpreted, that.lastInterpreted) &&
      Objects.equals(references, that.references) &&
      license == that.license &&
      Objects.equals(organismQuantity, that.organismQuantity) &&
      Objects.equals(organismQuantityType, that.organismQuantityType) &&
      Objects.equals(sampleSizeUnit, that.sampleSizeUnit) &&
      Objects.equals(sampleSizeValue, that.sampleSizeValue) &&
      Objects.equals(relativeOrganismQuantity, that.relativeOrganismQuantity) &&
      Objects.equals(identifiers, that.identifiers) &&
      Objects.equals(media, that.media) &&
      Objects.equals(facts, that.facts) &&
      Objects.equals(relations, that.relations) &&
      Objects.equals(identifiedByIds, that.identifiedByIds) &&
      Objects.equals(recordedByIds, that.recordedByIds) &&
      Objects.equals(occurrenceStatus, that.occurrenceStatus) &&
      Objects.equals(gadm, that.gadm) &&
      Objects.equals(institutionKey, that.institutionKey) &&
      Objects.equals(collectionKey, that.collectionKey) &&
      Objects.equals(isInCluster, that.isInCluster) &&
      Objects.equals(pathway, that.pathway) &&
      Objects.equals(degreeOfEstablishment, that.degreeOfEstablishment) &&
      Objects.equals(datasetID, that.datasetID) &&
      Objects.equals(datasetName, that.datasetName) &&
      Objects.equals(otherCatalogNumbers, that.otherCatalogNumbers) &&
      Objects.equals(recordedBy, that.recordedBy) &&
      Objects.equals(identifiedBy, that.identifiedBy) &&
      Objects.equals(preparations, that.preparations) &&
      Objects.equals(samplingProtocol, that.samplingProtocol);
  }

  @Override
  public int hashCode() {
    return Objects
      .hash(super.hashCode(), basisOfRecord, individualCount, sex, lifeStage, establishmentMeans,
        taxonKey, kingdomKey, phylumKey, classKey, orderKey, familyKey, genusKey, subgenusKey,
        speciesKey, acceptedTaxonKey, scientificName, acceptedScientificName, kingdom, phylum,
        clazz, order, family, genus, subgenus, species, genericName, specificEpithet,
        infraspecificEpithet, taxonRank, taxonomicStatus, dateIdentified, decimalLongitude,
        decimalLatitude, coordinatePrecision, coordinateUncertaintyInMeters, elevation,
        elevationAccuracy, depth, depthAccuracy, continent, country, stateProvince, waterBody, year,
        month, day, eventDate, typeStatus, typifiedName, issues, modified, lastInterpreted,
        references, license, organismQuantity, organismQuantityType, sampleSizeUnit,
        sampleSizeValue, relativeOrganismQuantity, identifiers, media, facts, relations, recordedByIds,
        identifiedByIds, occurrenceStatus, gadm, institutionKey, collectionKey, isInCluster,
        pathway, degreeOfEstablishment, datasetID, datasetName, otherCatalogNumbers, recordedBy, identifiedBy,
        preparations, samplingProtocol);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Occurrence.class.getSimpleName() + "[", "]")
      .add("basisOfRecord=" + basisOfRecord)
      .add("individualCount=" + individualCount)
      .add("sex=" + sex)
      .add("lifeStage=" + lifeStage)
      .add("establishmentMeans=" + establishmentMeans)
      .add("taxonKey=" + taxonKey)
      .add("kingdomKey=" + kingdomKey)
      .add("phylumKey=" + phylumKey)
      .add("classKey=" + classKey)
      .add("orderKey=" + orderKey)
      .add("familyKey=" + familyKey)
      .add("genusKey=" + genusKey)
      .add("subgenusKey=" + subgenusKey)
      .add("speciesKey=" + speciesKey)
      .add("acceptedTaxonKey=" + acceptedTaxonKey)
      .add("scientificName='" + scientificName + "'")
      .add("acceptedScientificName='" + acceptedScientificName + "'")
      .add("kingdom='" + kingdom + "'")
      .add("phylum='" + phylum + "'")
      .add("clazz='" + clazz + "'")
      .add("order='" + order + "'")
      .add("family='" + family + "'")
      .add("genus='" + genus + "'")
      .add("subgenus='" + subgenus + "'")
      .add("species='" + species + "'")
      .add("genericName='" + genericName + "'")
      .add("specificEpithet='" + specificEpithet + "'")
      .add("infraspecificEpithet='" + infraspecificEpithet + "'")
      .add("taxonRank=" + taxonRank)
      .add("taxonomicStatus=" + taxonomicStatus)
      .add("dateIdentified=" + dateIdentified)
      .add("decimalLongitude=" + decimalLongitude)
      .add("decimalLatitude=" + decimalLatitude)
      .add("coordinatePrecision=" + coordinatePrecision)
      .add("coordinateUncertaintyInMeters=" + coordinateUncertaintyInMeters)
      .add("coordinateAccuracy=" + coordinateAccuracy)
      .add("elevation=" + elevation)
      .add("elevationAccuracy=" + elevationAccuracy)
      .add("depth=" + depth)
      .add("depthAccuracy=" + depthAccuracy)
      .add("continent=" + continent)
      .add("country=" + country)
      .add("stateProvince='" + stateProvince + "'")
      .add("waterBody='" + waterBody + "'")
      .add("year=" + year)
      .add("month=" + month)
      .add("day=" + day)
      .add("eventDate=" + eventDate)
      .add("typeStatus=" + typeStatus)
      .add("typifiedName='" + typifiedName + "'")
      .add("issues=" + issues)
      .add("modified=" + modified)
      .add("lastInterpreted=" + lastInterpreted)
      .add("references=" + references)
      .add("license=" + license)
      .add("organismQuantity=" + organismQuantity)
      .add("organismQuantityType='" + organismQuantityType + "'")
      .add("sampleSizeUnit='" + sampleSizeUnit + "'")
      .add("sampleSizeValue=" + sampleSizeValue)
      .add("relativeOrganismQuantity=" + relativeOrganismQuantity)
      .add("identifiers=" + identifiers)
      .add("media=" + media)
      .add("facts=" + facts)
      .add("relations=" + relations)
      .add("recordedByIds=" + recordedByIds)
      .add("identifiedByIds=" + identifiedByIds)
      .add("occurrenceStatus=" + occurrenceStatus)
      .add("gadm=" + gadm)
      .add("institutionKey=" + institutionKey)
      .add("collectionKey=" + collectionKey)
      .add("isInCluster=" + isInCluster)
      .add("pathway=" + pathway)
      .add("degreeOfEstablishment=" + degreeOfEstablishment)
      .add("datasetID=" + datasetID)
      .add("datasetName=" + datasetName)
      .add("otherCatalogNumbers=" + otherCatalogNumbers)
      .add("recordedBy=" + recordedBy)
      .add("identifiedBy=" + identifiedBy)
      .add("preparations=" + preparations)
      .add("samplingProtocol=" + samplingProtocol)
      .toString();
  }

  /**
   * This private method is only for serialization via jackson and not exposed anywhere else!
   * It maps the verbatimField terms into properties with their simple name or qualified names for UnknownTerms.
   */
  @JsonAnyGetter
  private Map<String, String> jsonVerbatimFields() {
    Map<String, String> extendedProps = new HashMap<>();
    for (Map.Entry<Term, String> prop : getVerbatimFields().entrySet()) {
      Term t = prop.getKey();
      if (t instanceof UnknownTerm || PROPERTIES.contains(t.simpleName())) {
        extendedProps.put(t.qualifiedName(), prop.getValue());
      } else {
        // render all terms in controlled enumerations as simple names only - unless we have a property of that name already!
        extendedProps.put(t.simpleName(), prop.getValue());
      }
    }
    return extendedProps;
  }
}
