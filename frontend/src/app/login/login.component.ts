import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';

import { AuthenticationService, PageTitleService, UserDataService } from '../_services';

@Component({ templateUrl: 'login.component.html', styleUrls: ['./login.component.less'] })
export class LoginComponent implements OnInit {
    loginForm: FormGroup;
    returnUrl: string;
    submitted = false;
    loading = false;
    error = '';

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService,
        private userDataService: UserDataService,
        private pageTitle: PageTitleService
    ) {}

    ngOnInit() {
        this.pageTitle.removePageTitle();
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required],
        });

        // reset login status
        this.authenticationService.logout();

        // get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

        this.authenticationService.initRequest()
            .pipe(first())
            .subscribe(
                data => {
                    if (data.status === 'setup_required') {
                        this.router.navigate([ '/setup' ]);
                    }
                },
                console.error
            );
    }

    // convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    onSubmit() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.loginForm.invalid) {
            return;
        }

        this.loading = true;
        this.authenticationService.login(this.f.username.value, this.f.password.value)
            .pipe(first())
            .subscribe(
                result => {
                    if (result) {
                        this.router.navigate([this.returnUrl]);
                    } else {
                        this.error = 'Unknown error';
                    }
                    this.loading = false;
                },
                error => {
                    if (error.status === 401) {
                        this.error = 'Wrong credentials';
                    } else {
                        this.error = error.message;   
                    }
                    this.loading = false;
                }
            );
    }
}