import { Component, OnInit, ApplicationRef } from '@angular/core';
import { Router } from '@angular/router';
import { PageTitleService, UserDataService, JobService } from '../_services';
import { Job } from '../_models';

import { format } from 'timeago.js';

@Component({templateUrl: 'home.component.html', styleUrls: ['home.component.less']})
export class HomeComponent implements OnInit {

    addingJob = false;
    selectedJobID: number;

    constructor(
        private router: Router,
        private pageTitle: PageTitleService,
        public userData: UserDataService,
        private jobService: JobService
    ) {}

    ngOnInit() {
        this.pageTitle.setPageTitle('Home');
        this.userData.doInit().subscribe(() => {
          // do nothing  
        });
        this.jobService.addJobListener = () => {
            this.addingJob = true;
            this.selectedJobID = undefined;
        };
        this.jobService.selectJobListener = (jobID) => {
            this.addingJob = false;
            this.selectedJobID = jobID;
        }
    }

    selectJob(job: Job) {
        this.addingJob = false;
        this.selectedJobID = job.id;
    }
    
    getSelectedJob() {
        if (!this.userData.jobs) { 
            return;
        }
        
        for (let job of this.userData.jobs) {
            if (job.id === this.selectedJobID) {
                return job;
            }
        }
    }

    formatAgo(timestamp: number) {
        if (!timestamp) return;
        return format(new Date(timestamp), 'en_US');
    }

}