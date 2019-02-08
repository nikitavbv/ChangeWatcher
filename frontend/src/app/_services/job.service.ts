import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CreateJobResponse } from '../_models';

@Injectable()
export class JobService {

    addJobListener: any;
    selectJobListener: any;
    
    constructor(
        private http: HttpClient,
    ) {}

    triggerJobListener() {
        if (this.addJobListener) {
            this.addJobListener();
        }
    }

    getPreviewFor(url: String) {
        return this.http.post<any>('/api/v1/preview', { url });
    }

    getPreviewByID(screenshotID: String) {
        return this.http.get(`/api/v1/preview/${screenshotID}`, {
            responseType: 'blob'
        });
    }

    createJob(
        title: String, 
        url: String, 
        selectionX: Number, 
        selectionY: Number, 
        selectionWidth: Number, 
        selectionHeight: Number, 
        watchingInterval: Number,
        pixelDifferenceToTrigger: Number, 
        webhook: String
    ) {
        return this.http.post<CreateJobResponse>('/api/v1/jobs', {
            title,
            url,
            selectionX,
            selectionY,
            selectionWidth,
            selectionHeight,
            watchingInterval,
            pixelDifferenceToTrigger,
            webhook,
        });
    }

    deleteJob(
        jobID: number
    ) {
        return this.http.delete(`/api/v1/jobs/${jobID}`);
    }

}