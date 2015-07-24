package org.gbif.api.util.iterables;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.service.registry.NodeService;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pages through all organizations endorsed by a given node.
 */
public class NodeOrganizationPager extends EntityPager<Organization> {
    private static final Logger LOG = LoggerFactory.getLogger(NodeOrganizationPager.class);

    private final NodeService service;
    private final UUID endorsingNodeKey;

    public NodeOrganizationPager(NodeService service, UUID endorsingNodeKey, int pageSize) {
        super(pageSize);
        this.service = service;
        this.endorsingNodeKey = endorsingNodeKey;
    }

    @Override
    PagingResponse<Organization> nextPage(PagingRequest page) {
        return service.endorsedOrganizations(endorsingNodeKey, page);
    }

}
