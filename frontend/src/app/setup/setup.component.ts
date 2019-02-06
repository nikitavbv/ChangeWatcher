import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';

import { AuthenticationService, PageTitleService, UserDataService } from '../_services';

@Component({ templateUrl: 'setup.component.html', styleUrls: ['./setup.component.less'] })
export class SetupComponent implements OnInit {
    createUserForm: FormGroup;
    submitted = false;
    loading = false;
    error = '';

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService,
        private pageTitle: PageTitleService
    ) {}

    ngOnInit() {
        this.pageTitle.setPageTitle('Setup');
        this.createUserForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required],
            passwordRepeat: ['', Validators.required]
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.createUserForm.controls; }

    onSubmit() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.createUserForm.invalid) {
            return;
        }

        this.loading = false;
        this.authenticationService.createUser(this.f.username.value, this.f.password.value)
            .pipe(first())
            .subscribe(
                data => {
                    this.router.navigate(['/login']);
                },
                error => {
                    this.error = error.message;
                    this.loading = false;
                }
            );
    }

}