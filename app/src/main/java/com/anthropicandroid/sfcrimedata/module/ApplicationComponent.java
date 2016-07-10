package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/8/2016.
 */

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class})
public interface ApplicationComponent {

}
