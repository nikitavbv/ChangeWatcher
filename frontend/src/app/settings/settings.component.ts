import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageTitleService, AuthenticationService } from '../_services';

@Component({templateUrl: 'settings.component.html', styleUrls: ['settings.component.less']})
export class SettingsComponent implements OnInit {

    newUserName: string = '';
    newUserPassword: string = '';
    newUserPasswordRepeat: string = '';

    constructor(
        private pageTitle: PageTitleService,
        private authenticationService: AuthenticationService,
        private router: Router,
    ) {}

    ngOnInit() {
        this.pageTitle.setPageTitle('Settings');
    }

    addUser() {
        if (this.newUserName === '' || this.newUserPassword === '' || this.newUserPassword !== this.newUserPasswordRepeat) {
            return;
        }

        this.authenticationService.createUser(this.newUserName, this.newUserPassword)
            .subscribe(data => {
                this.newUserName = '';
                this.newUserPassword = '';
                this.newUserPasswordRepeat = '';
            }, console.error);
    }

    logout() {
        this.authenticationService.logout();
        this.router.navigate(['/']);
    }

}