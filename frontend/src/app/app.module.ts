import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { routing } from './app.routing';

import { AppComponent } from './app.component';
import { HeaderComponent } from './_shared';
import {HomeComponent, CreateJobComponent, ViewJobComponent, NoJobsYetNotifComponent} from './home';
import { SettingsComponent } from './settings';
import { LoginComponent } from './login';
import { SetupComponent } from './setup';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AuthGuard } from './_guards';
import { AuthenticationService, UserDataService, PageTitleService, JobService } from './_services';
import { JwtInterceptor } from './_helpers';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,

    HomeComponent,
    CreateJobComponent,
    ViewJobComponent,
    NoJobsYetNotifComponent,
    
    SettingsComponent,
    SetupComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
  ],
  providers: [
    AuthGuard,
    AuthenticationService,
    UserDataService,
    PageTitleService,
    JobService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
