package com.allback.cygipayment.service;

import com.allback.cygipayment.dto.request.ReservationReqDto;
import com.allback.cygipayment.dto.response.ReservationResDto;
import com.allback.cygipayment.entity.Reservation;
import com.allback.cygipayment.mapper.ReservationMapper;
import com.allback.cygipayment.repository.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;


    @Override
    public Long insertReservation(ReservationReqDto reservationReqDto) {
        Reservation reservation = reservationMapper.toEntity(reservationReqDto);
//        이미 존재하는 자리에 삽입을 하려 하면 -100을 반환 >> 실제 유저는 이 항목에서 True를 받을 수 없음
        boolean isExist = reservationRepository.existsByConcertIdAndSeat(reservation.getConcertId(), reservation.getSeat());
        log.info("[insertReservation] : 해당 자리가 존재하면 True | value : {}", isExist);
        if(isExist)
            return -100L;
        else {
            Reservation savedReservation = reservationRepository.save(reservation);
            log.info("[insertReservation] : 예약중 저장, reservationId : {}",
                    savedReservation.getReservationId());
            return savedReservation.getReservationId();
        }
    }

    @Override
    public void deleteReservationById(Long reservationId) {
        reservationRepository.deleteById(reservationId);
        log.info("[deleteReservationById] : 예약중 삭제, reservationId : {}",
            reservationId);
    }

    @Override
    public int getSoldSeatCnt(Long concertId) {
        int sold = reservationRepository.getSoldSeatCntByConcertId(concertId);
        log.info("[getSoldSeatCnt] : 예약중, 예약완료 좌석, soldSeatCnt : {}",
            sold);
        return sold;
    }

    @Override
    public List<String> getSoldSeatList(Long concertId) {
        List<String> seatList = reservationRepository.findSoldSeatByConcertId(concertId);
        log.info("[getSoldSeatList] : 예약중, 예약완료 좌석리스트, soldSeatCnt : {}",
            seatList.size());
        return seatList;
    }

    @Override
    public List<Integer> getSoldSeatCntList(List<Long> concertIdList) {
        List<Integer> soldList = new ArrayList<>();
        for (int i = 0; i < concertIdList.size(); i++) {
            int sold = reservationRepository.getSoldSeatCntByConcertId(concertIdList.get(i));
            soldList.add(sold);
        }
        return soldList;
    }



}
