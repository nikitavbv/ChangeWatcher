import { Component, Input } from '@angular/core';
import { Job } from 'src/app/_models';

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

    @Input() job: Job;

}