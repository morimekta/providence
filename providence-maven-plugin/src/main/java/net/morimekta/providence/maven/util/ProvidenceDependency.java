package net.morimekta.providence.maven.util;

import net.morimekta.providence.maven.plugin.ProvidenceAssemblyMojo;

import org.apache.maven.model.Dependency;

/**
 * Dependency with providence defaults.
 */
public class ProvidenceDependency extends Dependency {
    public ProvidenceDependency() {
        setType(ProvidenceAssemblyMojo.TYPE);
        setClassifier(ProvidenceAssemblyMojo.CLASSIFIER);
    }
}
