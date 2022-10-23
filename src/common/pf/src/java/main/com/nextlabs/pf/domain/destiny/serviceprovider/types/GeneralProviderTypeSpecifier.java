/*
 * Created on Aug 08, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.pf.domain.destiny.serviceprovider.types;

import java.util.Set;

import com.bluejungle.framework.patterns.EnumBase;

public class GeneralProviderTypeSpecifier extends EnumBase {
    public static final GeneralProviderTypeSpecifier USER = new GeneralProviderTypeSpecifier("USER");
    public static final GeneralProviderTypeSpecifier HOST = new GeneralProviderTypeSpecifier("HOST");
    public static final GeneralProviderTypeSpecifier APPLICATION = new GeneralProviderTypeSpecifier("APPLICATION");
    public static final GeneralProviderTypeSpecifier RESOURCE = new GeneralProviderTypeSpecifier("RESOURCE");

    private GeneralProviderTypeSpecifier(String name) {
        super(name, GeneralProviderTypeSpecifier.class);
    }

    public static GeneralProviderTypeSpecifier getElement(String name) {
        return getElement(name, GeneralProviderTypeSpecifier.class);
    }

    public static int numElements() {
        return numElements(GeneralProviderTypeSpecifier.class);
    }

    public static Set<GeneralProviderTypeSpecifier> elements() {
        return elements(GeneralProviderTypeSpecifier.class);
    }

    public static GeneralProviderTypeSpecifier forName(String name) {
        return getElement(name, GeneralProviderTypeSpecifier.class);
    }
}
