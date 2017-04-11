package WebService_KH30VP.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jws.WebService;

import WebService_KH30VP.ArrayOfSeat;
import WebService_KH30VP.CinemaException;
import WebService_KH30VP.ICinema;
import WebService_KH30VP.ICinemaBuyCinemaException;
import WebService_KH30VP.ICinemaGetAllSeatsCinemaException;
import WebService_KH30VP.ICinemaGetSeatStatusCinemaException;
import WebService_KH30VP.ICinemaInitCinemaException;
import WebService_KH30VP.ICinemaLockCinemaException;
import WebService_KH30VP.ICinemaReserveCinemaException;
import WebService_KH30VP.ICinemaUnlockCinemaException;
import WebService_KH30VP.Seat;
import WebService_KH30VP.SeatStatus;

@WebService(
		name="Cinema",
		portName="ICinema_HttpSoap11_Port",
		targetNamespace="http://www.iit.bme.hu/soi/hw/SeatReservation",
		endpointInterface="WebService_KH30VP.ICinema",
		wsdlLocation="WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema {

	private static ArrayOfSeat arrayOfSeat;
	private static int rows;
	private static int columns;
	private static Map<Seat, SeatStatus> seatStatus;
	private static Map<String, String> locks;
	
	@Override
	public void init(int rows, int columns) throws ICinemaInitCinemaException {
		if (rows < 1 || rows > 26 || columns < 1 || columns > 100) {
			throw new ICinemaInitCinemaException("invalid row or column count", new CinemaException());
		}
		
		Cinema.rows = rows;
		Cinema.columns = columns;
		arrayOfSeat = new ArrayOfSeat();
		seatStatus = new HashMap<>();
		locks = new HashMap<>();
		
		generateSeats();
	}
	
	private void generateSeats() {
		List<Seat> seats = arrayOfSeat.getSeat();
		for (int i = 0; i < rows; i++) {
			for (int j = 1; j <= columns; j++) {
				Seat seat = new Seat();
				seat.setRow("" + (char) (65 + i));
				seat.setColumn("" + j);
				seats.add(seat);
				
				seatStatus.put(seat, SeatStatus.FREE);
			}
		}
	}

	@Override
	public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
		return arrayOfSeat;
	}

	@Override
	public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
		Seat s;
		try {
			s = findSeat(seat);
		} catch (Exception e) {
			throw new ICinemaGetSeatStatusCinemaException(e.getMessage(), new CinemaException());
		}
		return seatStatus.get(s);
	}
	
	private Seat findSeat(Seat seat) throws Exception{
		for(Seat s: arrayOfSeat.getSeat()) {
			if (s.getRow().equals(seat.getRow()) && s.getColumn().equals(seat.getColumn())) {
				return s;
			}
		}
		throw new Exception("Unable to find seat.");
	}

	@Override
	public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
		try {
			Seat s = findSeat(seat);
			checkIfThereAreEnoughColumns(s, count);
			
			List<Seat> seats = arrayOfSeat.getSeat();
			int indexOfSeat = seats.indexOf(s);
			checkIfTheSeatsAre("Free", seats, indexOfSeat, count);
			
			Seat lastSeat = makeSeats("Locked", seats, indexOfSeat, count);
			String key = generateKey(s, lastSeat);
			locks.put(key, s.getRow() + " " + s.getColumn() + " " + lastSeat.getRow() + " " + lastSeat.getColumn());
			return key;
		} catch (Exception e) {
			throw new ICinemaLockCinemaException(e.getMessage(), new CinemaException());
		} 
	}
	

	private String generateKey(Seat s, Seat lastSeat) {
		String key = s.getColumn() + s.getRow() + lastSeat.getRow() + lastSeat.getColumn();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			key += random.nextInt(10);
		}
		return key;
	}

	private Seat makeSeats(String status, List<Seat> seats, int indexOfSeat, int count) {
		Seat lastSeat = null;
		for (int i = indexOfSeat; i < count + indexOfSeat; i++) {
			lastSeat = seats.get(i);
			seatStatus.remove(lastSeat);
			seatStatus.put(lastSeat, SeatStatus.fromValue(status));
		}
		return lastSeat;
	}

	private void checkIfTheSeatsAre(String status, List<Seat> seats, int indexOfSeat, int count) throws Exception {
		for (int i = indexOfSeat; i < count + indexOfSeat; i++) {
			if (seatStatus.get(seats.get(i)) != SeatStatus.fromValue(status)) {
				throw new Exception("Not all seats are " + status + ".");
			}
		}
	}
	
	private void checkIfThereAreEnoughColumns(Seat seat, int count) throws Exception {
		Integer column = Integer.parseInt(seat.getColumn());
		if (columns - column + 1 < count) {
			throw new Exception("There are not enough seats in the row.");
		}
	}

	@Override
	public void unlock(String lockId) throws ICinemaUnlockCinemaException {
		try {
			processIdAndMakeSeats("Free", lockId);
		} catch (Exception e) {
			throw new ICinemaUnlockCinemaException(e.getMessage(), new CinemaException());
		}
	}
	
	private void processIdAndMakeSeats(String status, String lockId) throws Exception {
		String[] seatInfo = getSeatInfo(lockId);
		int count = Integer.parseInt(seatInfo[3]) - Integer.parseInt(seatInfo[1]) + 1;
		Seat firstSeat = getNewSeat(seatInfo[0], seatInfo[1]);
		List<Seat> seats = arrayOfSeat.getSeat();
		int indexOfFirstSeat = seats.indexOf(findSeat(firstSeat));
		makeSeats(status, seats, indexOfFirstSeat, count);
	}
	
	private String[] getSeatInfo(String lockId) throws Exception {
		String[] seatInfo = locks.get(lockId).split(" ");
		if (seatInfo == null) {
			throw new Exception("Invalid key lock ID");
		}
		return seatInfo;
	}
	
	private Seat getNewSeat(String row, String column) {
		Seat seat = new Seat();
		seat.setRow(row);
		seat.setColumn(column);
		return seat;
	}

	@Override
	public void reserve(String lockId) throws ICinemaReserveCinemaException {
		try {
			processIdAndMakeSeats("Reserved", lockId);
		} catch (Exception e) {
			throw new ICinemaReserveCinemaException(e.getMessage(), new CinemaException());
		}
	}

	@Override
	public void buy(String lockId) throws ICinemaBuyCinemaException {
		try {
			processIdAndMakeSeats("Sold", lockId);
		} catch (Exception e) {
			throw new ICinemaBuyCinemaException(e.getMessage(), new CinemaException());
		}
	}

}
