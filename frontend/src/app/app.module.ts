import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ExchangesComponent} from './exchanges/exchanges.component';
import {RouterModule, Routes} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {ApiModule, Configuration, ConfigurationParameters} from 'crabi-ng-api';
import {environment} from "../environments/environment";

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.API_BASE_PATH
  };
  return new Configuration(params);
}


const appRoutes: Routes = [
  {path: 'exchanges', component: ExchangesComponent}
];


@NgModule({
  declarations: [
    AppComponent,
    ExchangesComponent
  ],
  imports: [
    RouterModule.forRoot(appRoutes, {enableTracing: true, useHash: true}),
    BrowserModule,
    HttpClientModule,
    ApiModule.forRoot(apiConfigFactory),
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
