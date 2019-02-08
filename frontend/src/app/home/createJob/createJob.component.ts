import { Component } from "@angular/core";
import { JobService, UserDataService } from 'src/app/_services';

declare let Cropper: any;

@Component({
    selector: 'create-job',
    templateUrl: 'createJob.component.html', 
    styleUrls: ['createJob.component.less']
})
export class CreateJobComponent {

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

    checkImageInterval: number = 1000;

    makingPreview: Boolean = false;
    cropper: any;
    previewLoaded: Boolean = false;
    selectionSaved: Boolean = false;

    jobName: String;
    webpageUrl: String;

    currentSelectionX: number = 0;
    currentSelectionY: number = 0;
    currentSelectionWidth: number = 0;
    currentSelectionHeight: number = 0;

    watchingInterval: number = 1000 * 60 * 60;
    changeThreshold: number = 0;
    notificationWebhookUrl: String;

    constructor(
        private jobService: JobService,
        private userData: UserDataService,
    ) {}

    ngAfterViewInit() {
        this.initCropper();
    }

    initCropper() {
        this.cropper = new Cropper(document.getElementById('webpage-preview-img'), {
            crop: this.onImageCrop.bind(this),
        });
    }

    previewPage() {
        this.makingPreview = true;
        this.previewLoaded = false;
        this.selectionSaved = false;

        if (this.cropper === undefined) {
            this.initCropper();
        }
        
        this.jobService.getPreviewFor(this.webpageUrl).subscribe(({ screenshotID }) => {
            const checkInterval = setInterval(() => {
                this.jobService.getPreviewByID(screenshotID).subscribe((data) => {
                    clearInterval(checkInterval);
                    this.makingPreview = false;
                    var reader = new FileReader();
                    reader.readAsDataURL(data); 
                    reader.onloadend = () => this.cropper.replace(reader.result);
                    this.previewLoaded = true;
                }, () => {});
            }, this.checkImageInterval);
        }, console.error);
    }

    onImageCrop(event) {
        this.currentSelectionX = Math.round(event.detail.x);
        this.currentSelectionY = Math.round(event.detail.y);
        this.currentSelectionWidth = Math.round(event.detail.width);
        this.currentSelectionHeight = Math.round(event.detail.height);
    }

    saveSelection() {
        this.selectionSaved = true;
        this.cropper.destroy();
        this.cropper = undefined;
    }

    saveJob() {
        this.jobService.createJob(
            this.jobName, 
            this.webpageUrl,
            this.currentSelectionX,
            this.currentSelectionY,
            this.currentSelectionWidth,
            this.currentSelectionHeight,
            this.watchingInterval,
            this.currentSelectionWidth * this.currentSelectionHeight * this.changeThreshold,
            this.notificationWebhookUrl
        ).subscribe(data => {
            this.userData.jobs = data.jobs;
            if (this.jobService.selectJobListener) {
                this.jobService.selectJobListener(data.newJobID);
            }
        }, console.error);
    }

}