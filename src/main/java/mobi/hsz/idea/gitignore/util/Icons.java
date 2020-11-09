// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * {@link Icons} class that holds icon resources.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.2.1
 */
public class Icons {
    /** General ignore icon. */
    public static final Icon IGNORE = IconLoader.getIcon("/icons/icon.png");

    /** Bazaar icon. */
    public static final Icon BAZAAR = IconLoader.getIcon("/icons/icon_bazaar.png");

    /** Chefignore icon. */
    public static final Icon CHEF = IconLoader.getIcon("/icons/icon_chef.png");

    /** Cloud Foundry icon. */
    public static final Icon CLOUD_FOUNDRY = IconLoader.getIcon("/icons/icon_cf.png");

    /** Cvsignore icon. */
    public static final Icon CVS = IconLoader.getIcon("/icons/icon_cvs.png");

    /** Darcs icon. */
    public static final Icon DARCS = IconLoader.getIcon("/icons/icon_darcs.png");

    /** Dockerignore icon. */
    public static final Icon DOCKER = IconLoader.getIcon("/icons/icon_docker.png");

    /** ESLint icon. */
    public static final Icon ESLINT = IconLoader.getIcon("/icons/icon_eslint.png");

    /** ElasticBeanstalk icon. */
    public static final Icon ELASTIC_BEANSTALK = IconLoader.getIcon("/icons/icon_elasticbeanstalk.png");

    /** Git icon. */
    public static final Icon GIT = IconLoader.getIcon("/icons/icon_git.png");

    /** Google Cloud icon. */
    public static final Icon GCLOUD = IconLoader.getIcon("/icons/icon_gcloud.png");

    /** Kubernetes Helm icon. */
    public static final Icon HELM = IconLoader.getIcon("/icons/icon_helm.png");

    /** Floobits icon. */
    public static final Icon FLOOBITS = IconLoader.getIcon("/icons/icon_floobits.png");

    /** Fossil icon. */
    public static final Icon FOSSIL = IconLoader.getIcon("/icons/icon_fossil.png");

    /** Mercurial icon. */
    public static final Icon MERCURIAL = IconLoader.getIcon("/icons/icon_mercurial.png");

    /** Jetpack icon. */
    public static final Icon JETPACK = IconLoader.getIcon("/icons/icon_jetpack.png");

    /** JSHint icon. */
    public static final Icon JSHINT = IconLoader.getIcon("/icons/icon_jshint.png");

    /** Monotone icon. */
    public static final Icon MONOTONE = IconLoader.getIcon("/icons/icon_monotone.png");

    /** Nodemon icon. */
    public static final Icon NODEMON = IconLoader.getIcon("/icons/icon_nodemon.png");

    /** Npmignore icon. */
    public static final Icon NPM = IconLoader.getIcon("/icons/icon_npm.png");

    /** NuxtJS icon. */
    public static final Icon NUXTJS = IconLoader.getIcon("/icons/icon_nuxtjs.png");

    /** Perforce icon. */
    public static final Icon PERFORCE = IconLoader.getIcon("/icons/icon_perforce.png");

    /** Prettier icon. */
    public static final Icon PRETTIER = IconLoader.getIcon("/icons/icon_prettier.png");

    /** StyleLint icon. */
    public static final Icon STYLELINT = IconLoader.getIcon("/icons/icon_stylelint.png");

    /** Stylint icon. */
    public static final Icon STYLINT = IconLoader.getIcon("/icons/icon_stylint.png");

    /** Swagger Codegen icon. */
    public static final Icon SWAGGER_CODEGEN = IconLoader.getIcon("/icons/icon_swagger-codegen.png");

    /** TeamFoundation icon. */
    public static final Icon TF = IconLoader.getIcon("/icons/icon_tf.png");

    /** Up icon. */
    public static final Icon UP = IconLoader.getIcon("/icons/icon_up.png");

    /** Private constructor to prevent creating {@link Icons} instance. */
    private Icons() {
    }
}
