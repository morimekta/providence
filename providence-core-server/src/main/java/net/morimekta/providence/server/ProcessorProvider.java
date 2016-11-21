package net.morimekta.providence.server;

import net.morimekta.providence.PProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * Processor provider for generating per-request service processors in HTTP
 * servlets.
 */
@FunctionalInterface
public interface ProcessorProvider {
    PProcessor processorForRequest(HttpServletRequest request);
}
