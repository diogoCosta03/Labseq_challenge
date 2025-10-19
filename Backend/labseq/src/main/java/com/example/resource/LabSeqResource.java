package com.example.resource;

import com.example.dto.CacheStatsResponse;
import com.example.dto.ErrorResponse;
import com.example.dto.LabSeqResponse;
import com.example.dto.MessageResponse;
import com.example.service.LabSeqService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.math.BigInteger;

/**
 * REST Resource for the labseq sequence service.
 *
 * This is the equivalent of @RestController in Spring Boot.
 * In Quarkus, we use JAX-RS annotations (@Path, @GET, @POST, etc.)
 *
 * Available endpoints:
 * - GET  /labseq/{n}         - Calculate l(n)
 * - GET  /labseq/cache/stats - Get cache statistics
 * - DELETE /labseq/cache     - Clear the cache
 */
@Path("/labseq")
@Tag(name = "LabSeq Service", description = "Calculate values from the labseq sequence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LabSeqResource {

    private static final Logger LOG = Logger.getLogger(LabSeqResource.class);

    @Inject
    LabSeqService labSeqService;

    /**
     * GET /labseq/{n}
     *
     * Calculate and return the labseq sequence value at index n.
     *
     * Example: GET /labseq/10
     * Response: {"index":10,"value":"3","calculationTimeMs":2,"cacheSize":11}
     *
     * @param n the index in the sequence (non-negative integer)
     * @return JSON with calculated value and metadata
     */
    @GET
    @Path("/{n}")
    @Operation(
            summary = "Get labseq value at index n",
            description = "Calculate and return the value of the labseq sequence at the given index. " +
                    "The sequence is defined as: l(0)=0, l(1)=1, l(2)=0, l(3)=1, l(n)=l(n-4)+l(n-3) for n>3. " +
                    "Results are cached for improved performance on subsequent calls."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Successfully calculated the sequence value",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = LabSeqResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input (negative number or invalid format)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Internal server error during calculation",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response getLabSeqValue(
            @Parameter(
                    description = "Index in the sequence (non-negative integer)",
                    required = true,
                    example = "10"
            )
            @PathParam("n") int n
    ) {
        LOG.infof("Received request to calculate l(%d)", n);

        try {
            // Measure calculation time
            long startTime = System.currentTimeMillis();
            BigInteger result = labSeqService.calculate(n);
            long calculationTime = System.currentTimeMillis() - startTime;

            // Build response
            LabSeqResponse response = new LabSeqResponse(
                    n,
                    result.toString(),
                    calculationTime,
                    labSeqService.getCacheSize()
            );

            LOG.infof("Successfully calculated l(%d) in %dms", n, calculationTime);
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            LOG.warnf("Invalid input for n=%d: %s", n, e.getMessage());

            ErrorResponse error = new ErrorResponse(
                    "Invalid input",
                    e.getMessage(),
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();

        } catch (Exception e) {
            LOG.errorf(e, "Error calculating l(%d)", n);

            ErrorResponse error = new ErrorResponse(
                    "Calculation error",
                    "An unexpected error occurred: " + e.getMessage(),
                    500
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    /**
     * GET /labseq/cache/stats
     *
     * Returns statistics about the current cache state.
     *
     * Example: GET /labseq/cache/stats
     * Response: {"cacheSize":11}
     *
     * @return JSON with cache statistics
     */
    @GET
    @Path("/cache/stats")
    @Operation(
            summary = "Get cache statistics",
            description = "Returns information about the current state of the cache, including number of cached values"
    )
    @APIResponse(
            responseCode = "200",
            description = "Cache statistics retrieved successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CacheStatsResponse.class)
            )
    )
    public Response getCacheStats() {
        LOG.debug("Cache stats requested");

        CacheStatsResponse stats = new CacheStatsResponse(
                labSeqService.getCacheSize()
        );

        return Response.ok(stats).build();
    }

    /**
     * DELETE /labseq/cache
     *
     * Clears the cache, removing all values except base cases (l(0) to l(3)).
     *
     * Example: DELETE /labseq/cache
     * Response: {"message":"Cache cleared successfully","cacheSize":4}
     *
     * @return JSON confirming the cache was cleared
     */
    @DELETE
    @Path("/cache")
    @Operation(
            summary = "Clear the cache",
            description = "Removes all cached values except base values (l(0) to l(3)). " +
                    "Use this to free memory or reset the cache state."
    )
    @APIResponse(
            responseCode = "200",
            description = "Cache cleared successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = MessageResponse.class)
            )
    )
    public Response clearCache() {
        LOG.info("Cache clear requested");

        int previousSize = labSeqService.getCacheSize();
        labSeqService.clearCache();
        int newSize = labSeqService.getCacheSize();

        LOG.infof("Cache cleared: %d -> %d entries", previousSize, newSize);

        MessageResponse message = new MessageResponse(
                "Cache cleared successfully",
                newSize
        );

        return Response.ok(message).build();
    }}