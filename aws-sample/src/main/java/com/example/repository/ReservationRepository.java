package com.example.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	Optional<Reservation> findByIdAndDeleteDateTimeIsNull(Long id);

	List<Reservation> findByDeleteDateTimeIsNull();
	List<Reservation> findByMemberIdAndDeleteDateTimeIsNull(Long memberId);
	List<Reservation> findByFacilityIdAndDeleteDateTimeIsNull(Long facilityId);

	List<Reservation> findByFacilityIdAndRdayAndDeleteDateTimeIsNull(Long facilityId, LocalDate r_day); //メソッド名にアンダーバーはNG

	//　対象会員かつ　（予約日が今日かつ予約終了時間が現在以降　もしくは　予約日が明日以降）かつ未削除
    @Query("SELECT r FROM Reservation r WHERE r.memberId = :memberId AND ((r.rday = :rday AND r.rend >= :rend) OR r.rday > :rday) AND r.deleteDateTime IS NULL ORDER BY r.rday ASC, r.rend ASC")
    public List<Reservation> findRemainByMemberId(Long memberId, LocalDate rday, LocalTime rend);

	//　対象会員かつ　（予約日が今日かつ予約終了時間が現在より前　もしくは　予約日が昨日以前）かつ未削除
    @Query("SELECT r FROM Reservation r WHERE r.memberId = :memberId AND ((r.rday = :rday AND r.rend < :rend) OR r.rday < :rday) AND r.deleteDateTime IS NULL ORDER BY r.rday ASC, r.rend ASC")
    public List<Reservation> findPastByMemberId(Long memberId, LocalDate rday, LocalTime rend);

	//　対象施設かつ　（予約日が今日かつ予約終了時間が現在以降　もしくは　予約日が明日以降）かつ未削除
    @Query("SELECT r FROM Reservation r WHERE r.facilityId = :facilityId AND ((r.rday = :rday AND r.rend >= :rend) OR r.rday > :rday) AND r.deleteDateTime IS NULL")
    public List<Reservation> findRemainByFacilityId(Long facilityId, LocalDate rday, LocalTime rend);

}
