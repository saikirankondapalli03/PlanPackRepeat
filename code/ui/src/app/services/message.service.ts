import { Injectable } from '@angular/core';
import { Observable,Subject} from 'rxjs';

@Injectable()
export class MessageService {
    private subject = new Subject<any>();

    sendMessage(message: string) {
        console.log("In Send Message" , message)
        this.subject.next({ text: message });
    }

    clearMessage() {
        this.subject.next();
    }

    getMessage() :Observable<any>{
        return this.subject;
    }
}
