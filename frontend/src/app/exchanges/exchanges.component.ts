import {Component, OnInit} from '@angular/core';
import {ExchangesService} from "../exchanges.service";

@Component({
  selector: 'app-exchanges',
  templateUrl: './exchanges.component.html',
  styleUrls: ['./exchanges.component.css']
})
export class ExchangesComponent implements OnInit {

  exchanges;

  constructor(private exchangesService: ExchangesService) {
  }

  ngOnInit() {
    this.exchanges = this.exchangesService.getExchanges();
  }

  connectToExchange(exchange) {
    this.exchangesService.connectToExchange(exchange).subscribe({
      next() {
        console.log("connected")
      },
      complete() {
        console.log("completed")
      }
    });
  }

}
