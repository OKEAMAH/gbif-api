package org.gbif.api.service.collections;

import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.gbif.api.model.collections.descriptors.Descriptor;
import org.gbif.api.model.collections.descriptors.DescriptorGroup;
import org.gbif.api.model.collections.request.DescriptorSearchRequest;
import org.gbif.api.model.collections.request.DescriptorGroupSearchRequest;
import org.gbif.api.model.common.export.ExportFormat;
import org.gbif.api.model.common.paging.PagingResponse;

/** API service to work with collection descriptors. */
public interface DescriptorsService {

  /**
   * Creates a new descriptor group.
   *
   * <p>// TODO
   *
   * @return key of the created descriptor group.
   */
  long createDescriptorGroup(
      @NotNull @Valid byte[] descriptorsGroupFile,
      @NotNull ExportFormat format,
      @NotNull String title,
      String description,
      @NotNull UUID collectionKey);

  /**
   * Deletes a descriptor group by key.
   *
   * @param key of the descriptor group to be deleted.
   */
  void deleteDescriptorGroup(@NotNull long key);

  /**
   * Retrieves a descriptor group by its key.
   *
   * @param key of the descriptor group to be retrieved.
   * @return the descriptor group
   */
  DescriptorGroup getDescriptorGroup(@NotNull long key);

  /**
   * Updates an existing descriptor group.
   *
   * @param // TODO
   */
  void updateDescriptorGroup(
      @NotNull long descriptorGroupKey,
      @NotNull byte[] descriptorsGroupFile,
      @NotNull ExportFormat format,
      @NotNull String title,
      String description);

  /**
   * Pages {@link DescriptorGroup} entities based on the parameters received.
   *
   * @param searchRequest {@link DescriptorGroupSearchRequest} with all the parameters
   * @return a list of entities ordered by their creation date, newest coming first
   */
  PagingResponse<DescriptorGroup> listDescriptorGroups(
      @NotNull UUID collectionKey, DescriptorGroupSearchRequest searchRequest);

  /**
   * Retrieves a descriptor by its key.
   *
   * @param key of the descriptor to be retrieved.
   * @return the descriptor
   */
  Descriptor getDescriptor(@NotNull long key);

  /**
   * Pages {@link Descriptor} entities based on the parameters received.
   *
   * @param searchRequest {@link DescriptorSearchRequest} with all the parameters
   * @return a list of entities ordered by their creation date, newest coming first
   */
  PagingResponse<Descriptor> listDescriptors(DescriptorSearchRequest searchRequest);
}
