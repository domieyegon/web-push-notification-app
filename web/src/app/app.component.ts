import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MessageService } from './service/message.service';
import { SwPush } from '@angular/service-worker';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'web';

  constructor(
    private _swPush:SwPush,
    private messageService:MessageService,
    private breakPointObserver: BreakpointObserver,
  ){

    let device = this.breakPointObserver.isMatched(Breakpoints.Handset)? 'MOBILE' :
                 this.breakPointObserver.isMatched(Breakpoints.Tablet)? 'TABLET' :
                 this.breakPointObserver.isMatched(Breakpoints.Web)? 'DESKTOP': 'UNKNOWN';
    console.log(device);

    // this.breakPointObserver.observe([Breakpoints.Handset]).subscribe(result=> {
    //   console.log(result);
    // })
  }

  ngOnInit(): void {
    this.checkIsSubscribeToPushNotifications();
  }

  checkIsSubscribeToPushNotifications(){
    this._swPush.subscription.subscribe((subscription) => {
      if (!subscription){
        this.requestSubscription();
      }
    });
  }


  requestSubscription(): void {
    if(!this._swPush.isEnabled) {
      console.log("Notifications is not enabled");
      return;
    }

    this.messageService.getPublicKey().subscribe({
      next: (res)=> {
        const publicKey:string = res.body?.publicKey || '';
        this._swPush.requestSubscription({
          serverPublicKey: publicKey
        }).then((res)=> {
          console.log("Successfully subscribed to push notifications");
          this.saveSubscription(res);
          
        }).catch((err)=> console.log(err));

      },
      error: (err) =>{
        console.log(err.message)
      }
    });
  }
  
  saveSubscription(subscription: any) {
    this.messageService.subscribe(subscription).subscribe({
      next: (res)=> {
        console.log(res);
      },
      error: (err)=> {
        console.log(err)
      }
    })
  }


}
