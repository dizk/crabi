import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ExchangesService {

  constructor(private http: HttpClient) {
  }

  getExchanges() {
    return this.http.get("/api/exchanges")
  }

  connectToExchange(exchange) {
    return this.http.post(`/api/exchanges/${exchange.exchangeClassName}/connect`, {})
  }
}



