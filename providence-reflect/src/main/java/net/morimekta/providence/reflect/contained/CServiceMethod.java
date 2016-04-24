package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PServiceMethod;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Descriptor for a single service method.
 */
public class CServiceMethod implements PServiceMethod<CStruct, CField, CUnion, CField>,
                                       CAnnotatedDescriptor {
    private final String              name;
    private final boolean             oneway;
    private final CStructDescriptor   requestType;
    private final CUnionDescriptor    responseType;
    private final String              comment;
    private final Map<String, String> annotations;

    public CServiceMethod(String comment,
                          String name,
                          boolean oneway,
                          CStructDescriptor requestType,
                          CUnionDescriptor responseType,
                          Map<String, String> annotations) {
        this.comment = comment;
        this.name = name;
        this.oneway = oneway;
        this.requestType = requestType;
        this.responseType = responseType;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public boolean isOneway() {
        return oneway;
    }

    public CStructDescriptor getRequestType() {
        return requestType;
    }

    public CUnionDescriptor getResponseType() {
        return responseType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        if (annotations != null) {
            return annotations.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean hasAnnotation(String name) {
        if (annotations != null) {
            return annotations.containsKey(name);
        }
        return false;
    }

    @Override
    public String getAnnotationValue(String name) {
        if (annotations != null) {
            return annotations.get(name);
        }
        return null;
    }

    @Override
    public String getComment() {
        return comment;
    }
}
