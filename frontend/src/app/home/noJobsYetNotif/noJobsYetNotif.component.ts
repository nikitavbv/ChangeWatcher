import {Component} from "@angular/core";
import {JobService} from "../../_services";

@Component({
  selector: 'no-jobs-yet-notif',
  templateUrl: 'noJobsYetNotif.component.html',
  styleUrls: ['noJobsYetNotif.component.less']
})
export class NoJobsYetNotifComponent {

  constructor(private jobService: JobService) {}

  createJob() {
    this.jobService.triggerJobListener();
  }
}
