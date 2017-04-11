package cinema;

import javax.xml.ws.BindingProvider;

import WebService_KH30VP.CinemaService;
import WebService_KH30VP.ICinema;
import WebService_KH30VP.ICinemaBuyCinemaException;
import WebService_KH30VP.ICinemaInitCinemaException;
import WebService_KH30VP.ICinemaLockCinemaException;
import WebService_KH30VP.ICinemaReserveCinemaException;
import WebService_KH30VP.Seat;

public class Program {
	public static void main(String[] args) {
		CinemaService cinemaService = new CinemaService();
		ICinema seatReservation = cinemaService.getICinemaHttpSoap11Port();
		BindingProvider bp = (BindingProvider) seatReservation;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, args[0]);
		
		try {
			seatReservation.init(26, 100);
			Seat seat = new Seat();
			seat.setRow(args[1]);
			seat.setColumn(args[2]);
			if (args[3].equals("Lock")) {
				seatReservation.lock(seat, 1);
			} else if (args[3].equals("Reserve")) {
				String lockId = seatReservation.lock(seat, 1);
				seatReservation.reserve(lockId);
			} else if (args[3].equals("Buy")) {
				String lockId = seatReservation.lock(seat, 1);
				seatReservation.buy(lockId);
			}
		} catch (ICinemaInitCinemaException e) {
			e.printStackTrace();
		} catch (ICinemaLockCinemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ICinemaReserveCinemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ICinemaBuyCinemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
