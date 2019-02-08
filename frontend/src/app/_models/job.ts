export class Job {

    id: number;
    title: string;
    url: string;

    selectionX: number;
    selectionY: number;
    selectionWidth: number;
    selectionHeight: number;

    watchingInterval: number;
    pixelDifferenceToTrigger: number;
    
    webhook: string;

}