import { Component, OnInit, ApplicationRef } from '@angular/core';
import { first } from 'rxjs/operators';
import { Router } from '@angular/router';
import { PageTitleService, UserDataService } from '../_services';

@Component({templateUrl: 'home.component.html', styleUrls: ['home.component.less']})
export class HomeComponent implements OnInit {

    constructor(
        private router: Router,
        private pageTitle: PageTitleService,
        public userData: UserDataService
    ) {}

    ngOnInit() {
        this.pageTitle.setPageTitle('Home');
        this.userData.doInit().subscribe(() => {
          // do nothing  
        });
    }

}