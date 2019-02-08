import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

import { Job, InitResponse } from '../_models';

@Injectable()
export class UserDataService {
    isAdmin: boolean;
    jobs: Job[];

    constructor(private http: HttpClient) {}

    doInit() {
        return this.http.get<InitResponse>('/api/v1/init').pipe(map((res: InitResponse) => {
            this.jobs = res.jobs;
        }));
    }

    removeJobByID(id: number) {
        this.jobs = this.jobs.filter(j => j.id != id);
    }

}