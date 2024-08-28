/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.direct.DirectModelAccess;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess;

/**
 * A class that is responsible for interacting with the {@link ModelService}.
 *
 */
@ApplicationScoped
public class ModelServiceHelper {

    @Inject
    private ModelService modelServiceInstance;

    /**
     * Method that returns all the models info corresponding to the given model URN.
     *
     * @param modelURN
     *            - model URN.
     * @return {@link Collection<ModelInfo>}
     */
    public Collection<ModelInfo> getAllModels(final String modelURN) {
        final ModelMetaInformation modelMetaInfo = modelServiceInstance.getModelMetaInformation();
        return modelMetaInfo.getLatestModelsFromUrn(modelURN);
    }

    /**
     * Method that returns an API that can be used retrieve model contents.
     *
     * @return {@link DirectModelAccess}
     */
    public DirectModelAccess getDirectAccess() {
        return modelServiceInstance.getDirectAccess();
    }

    /**
     * Method that returns an API that can be used retrieve model Typed Access.
     *
     * @return {@link TypedModelAccess}
     */
    public TypedModelAccess getTypeModelAccess() {
        return modelServiceInstance.getTypedAccess();
    }
}
