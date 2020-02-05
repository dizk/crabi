import {Component, OnInit} from '@angular/core';
import {DefaultService, Exchange} from 'crabi-ng-api';

@Component({
  selector: 'app-exchanges',
  templateUrl: './exchanges.component.html',
  styleUrls: ['./exchanges.component.css']
})
export class ExchangesComponent implements OnInit {

  exchanges;

  constructor(private apiGateway: DefaultService) {
  }

  ngOnInit() {
    this.exchanges = this.apiGateway.getExchanges();
  }

  connectToExchange(exchange: Exchange) {
    this.apiGateway.connectToExchange(exchange).subscribe({
      next() {
        console.log("connected")
      },
      complete() {
        console.log("completed")
      }
    });
  }

}
