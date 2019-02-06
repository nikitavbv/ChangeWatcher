import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home';
import { SetupComponent } from './setup';
import { LoginComponent } from './login';
import { AuthGuard } from './_guards';
import { SettingsComponent } from './settings';

const appRoutes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'setup', component: SetupComponent },

    { path: '' , component: HomeComponent, canActivate: [AuthGuard] },
    { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard] },
    
    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);