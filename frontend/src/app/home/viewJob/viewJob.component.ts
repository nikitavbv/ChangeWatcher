import { Component, Input } from '@angular/core';
import { Job } from 'src/app/_models';
import { JobService, UserDataService } from 'src/app/_services';

@Component({
    selector: 'view-job',
    templateUrl: 'viewJob.component.html',
    styleUrls: ['viewJob.component.less']
})
export class ViewJobComponent {

    watchingIntervals = {
        [1000 * 60 * 60]: 'Every hour',
        [1000 * 60 * 60 * 6]: 'Every 6 hours',
        [1000 * 60 * 60 * 12]: 'Every 12 hours',
        [1000 * 60 * 60 * 24]: 'Every day',
        [1000 * 60 * 60 * 24 * 2]: 'Every 2 days',
        [1000 * 60 * 60 * 24 * 7]: 'Every week',
        [1000 * 60 * 60 * 24 * 28]: 'Every month',
    };
    objectKeys = Object.keys;

    @Input() job: Job;
    editingJob: Boolean = false;

    constructor(
        private jobService: JobService,
        private userData: UserDataService,
    ) {}

    editJob() {
        this.editingJob = true;
    }

    deleteJob() {
        this.jobService.deleteJob(this.job.id)
            .subscribe(() => {
                this.userData.removeJobByID(this.job.id)
                if (this.jobService.selectJobListener) {
                    this.jobService.selectJobListener(null);
                }
            }, console.error);
    }

    saveEdits() {
        this.jobService.updateJob(
            this.job.id,
            this.job.title,
            this.job.url,
            this.job.pixelDifferenceToTrigger,
            this.job.watchingInterval,
            this.job.webhook,
        ).subscribe(() => {
            this.editingJob = false;
        }, console.error);
    }

}