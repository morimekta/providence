package net.morimekta.providence.generator.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class FactoryLoader<Factory> {
    private final String manifestProperty;

    public FactoryLoader(String manifestProperty) {
        this.manifestProperty = manifestProperty;
    }

    public List<Factory> getFactories(File path) {
        try {
            List<File> jars = findJarFiles(path);
            List<Factory> factories = new ArrayList<>();
            for (File jar : jars) {
                URLClassLoader classLoader = getClassLoader(jar);
                Factory factory = getFactory(classLoader);
                if (factory != null) {
                    factories.add(factory);
                }
            }
            return factories;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public Factory getFactory(File file) {
        try {
            return getFactory(getClassLoader(file));
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private List<File> findJarFiles(File path) {
        List<File> out = new ArrayList<>();
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".jar")) {
                    out.add(file);
                }
            }
        }
        return out;
    }

    private URLClassLoader getClassLoader(File file) throws MalformedURLException {
        return URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader());
    }

    private Factory getFactory(URLClassLoader classLoader)
            throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        URL url = classLoader.findResource("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(url.openStream());
        String factoryClass = manifest.getMainAttributes().getValue(manifestProperty);
        if (factoryClass == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Class<Factory> klass = (Class<Factory>) classLoader.loadClass(factoryClass);
        return klass.newInstance();
    }
}
