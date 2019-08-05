import {Component} from "@angular/core";
import {JobService} from "../../_services";

@Component({
  selector: 'create-first-job-message',
  templateUrl: 'createFirstJobMessage.component.html',
  styleUrls: ['createFirstJobMessage.component.less']
})
export class CreateFirstJobMessageComponent {

  constructor(private jobService: JobService) {}

  createJob() {
    this.jobService.triggerJobListener();
  }
}
