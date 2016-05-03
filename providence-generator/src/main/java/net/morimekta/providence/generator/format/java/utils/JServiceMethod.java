package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.util.Strings;

import java.util.ArrayList;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;

/**
 * Created by morimekta on 4/24/16.
 */
public class JServiceMethod {
    private final CService service;
    private final CServiceMethod method;
    private final JHelper helper;

    public JServiceMethod(CService service,
                          CServiceMethod method,
                          JHelper helper) {
        this.service = service;
        this.method = method;
        this.helper = helper;
    }

    public CServiceMethod getMethod() {
        return method;
    }

    public CService getService() {
        return service;
    }

    public String constant() {
        return Strings.c_case("", method.getName()).toUpperCase();
    }

    public String name() {
        return method.getName();
    }

    public String methodName() {
        return JUtils.camelCase(method.getName());
    }

    public String getRequestClass() {
        return camelCase("", method.getRequestType().getName());
    }

    public String getResponseClass() {
        if (method.getResponseType() != null) {
            return camelCase("", method.getResponseType().getName());
        }
        return null;
    }

    public JField getResponse() {
        if (method.getResponseType() != null) {
            if (method.getResponseType().getField(0) != null) {
                return new JField(method.getResponseType().getField(0),
                                  helper,
                                  0);
            }
        }
        return null;
    }

    public JField[] params() {
        CField[] fields = method.getRequestType().getFields();
        JField[] ret = new JField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            ret[i] = new JField(fields[i], helper, i);
        }
        return ret;
    }

    public JField[] exceptions() {
        if (method.getResponseType() == null) return new JField[0];
        ArrayList<JField> ret = new ArrayList<>();

        int idx = 0;
        for (CField field : method.getResponseType().getFields()) {
            if (field.getKey() != 0) {
                ret.add(new JField(field, helper, idx++));
            }
        }
        return ret.toArray(new JField[ret.size()]);
    }
}
